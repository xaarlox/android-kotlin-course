package com.xaarlox.task13_microgrid_flow.data

import com.xaarlox.task13_microgrid_flow.domain.EventLevel
import com.xaarlox.task13_microgrid_flow.domain.SensorException
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import com.xaarlox.task13_microgrid_flow.domain.SensorSample
import com.xaarlox.task13_microgrid_flow.domain.SourceStatus
import com.xaarlox.task13_microgrid_flow.domain.StreamEvent
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MicrogridSimulator(
    private val scope: CoroutineScope,
    private val faults: FaultController,
    private val generator: SignalGenerator = SignalGenerator(),
    private val clock: () -> Long = System::currentTimeMillis,
    private val backoffMs: (consecutiveFailures: Int) -> Long = ::defaultBackoffMs
) {
    private val hotFlows: Map<SensorKind, MutableSharedFlow<SensorSample>> =
        SensorKind.entries.associateWith {
            MutableSharedFlow<SensorSample>(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }

    private val _statuses = MutableStateFlow<Map<SensorKind, SourceStatus>>(
        SensorKind.entries.associateWith { SourceStatus.Connecting }
    )
    val statuses: StateFlow<Map<SensorKind, SourceStatus>> = _statuses.asStateFlow()

    private val _events = MutableStateFlow<List<StreamEvent>>(emptyList())
    val events: StateFlow<List<StreamEvent>> = _events.asStateFlow()

    private val nextEventId = AtomicLong(0)
    private var started = false

    /** The hot flow of values of the given sensor */
    fun stream(kind: SensorKind): SharedFlow<SensorSample> = hotFlows.getValue(kind)

    /** Starts all five producers. Calling it again does nothing */
    fun start() {
        if (started) return
        started = true
        SensorKind.entries.forEach { startProducer(it) }
    }

    /** Adds an entry to the event log (the latest [MAX_EVENTS] entries, newest first) */
    fun report(level: EventLevel, message: String) {
        val event = StreamEvent(nextEventId.incrementAndGet(), clock(), level, message)
        _events.update { (listOf(event) + it).take(MAX_EVENTS) }
    }

    /**
     * The cold flow of readings of one sensor: waits for the period, checks whether a fault
     * is pending and generates a value. Throws [SensorException] during a fault
     */
    private fun readings(kind: SensorKind): Flow<SensorSample> = flow {
        while (true) {
            delay(kind.periodMs)
            val value = when (faults.nextFault(kind)) {
                FaultType.NO_SIGNAL -> throw SensorException(kind, "немає сигналу від датчика")
                FaultType.CORRUPTED_VALUE -> Double.NaN
                null -> generator.next(kind, clock(), currentBalanceKw())
            }
            emit(SensorSample(kind, value, clock()))
        }
    }

    /** Validates a value; invalid data is treated as a sensor error */
    private fun validate(sample: SensorSample): SensorSample {
        if (!sample.value.isFinite() || sample.value < 0.0) {
            throw SensorException(sample.kind, "отримано некоректне значення")
        }
        return sample
    }

    private fun startProducer(kind: SensorKind) {
        val hot = hotFlows.getValue(kind)
        var consecutiveFailures = 0

        scope.launch {
            readings(kind)
                .map { validate(it) }
                .retryWhen { cause, _ ->
                    consecutiveFailures++
                    val pause = backoffMs(consecutiveFailures)
                    val reason = cause.message ?: "невідома помилка"
                    _statuses.update {
                        it + (kind to SourceStatus.Reconnecting(consecutiveFailures, reason))
                    }
                    report(
                        EventLevel.WARNING,
                        "${kind.title}: $reason. Спроба №$consecutiveFailures через $pause мс"
                    )
                    delay(pause)
                    true // true = collect the cold flow again (automatic recovery)
                }
                .collect { sample ->
                    if (consecutiveFailures > 0) {
                        report(EventLevel.SUCCESS, "${kind.title}: з'єднання відновлено")
                        consecutiveFailures = 0
                    }
                    _statuses.update { it + (kind to SourceStatus.Online) }
                    hot.emit(sample) // publish to the hot flow
                }
        }
    }

    /** Current balance (generation − load) based on the latest values of the hot flows */
    private fun currentBalanceKw(): Double {
        fun latest(kind: SensorKind) =
            hotFlows.getValue(kind).replayCache.firstOrNull()?.value ?: 0.0
        return latest(SensorKind.SOLAR) + latest(SensorKind.WIND) - latest(SensorKind.LOAD)
    }

    companion object {
        const val MAX_EVENTS = 8
    }
}

/** Pause before a retry: 500, 1000, 2000, 4000 ms and then 4000 ms again */
fun defaultBackoffMs(consecutiveFailures: Int): Long =
    500L shl (consecutiveFailures - 1).coerceIn(0, 3)