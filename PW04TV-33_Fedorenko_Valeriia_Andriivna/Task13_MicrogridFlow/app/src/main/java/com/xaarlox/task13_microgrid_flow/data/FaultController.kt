package com.xaarlox.task13_microgrid_flow.data

import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The kind of simulated sensor fault */
enum class FaultType { NO_SIGNAL, CORRUPTED_VALUE }

/**
 * Controls the simulation of sensor faults so that automatic reconnection can be demonstrated clearly
 *
 *  - [inject] — forces the next N reads of the chosen sensor to fail (a button in the UI);
 *  - [setRandomFaults] — enables random faults with a small probability
 */
class FaultController(private val random: Random = Random.Default) {

    private val pendingFailures = ConcurrentHashMap<SensorKind, AtomicInteger>()

    private val _randomFaults = MutableStateFlow(false)
    val randomFaults: StateFlow<Boolean> = _randomFaults.asStateFlow()

    fun setRandomFaults(enabled: Boolean) {
        _randomFaults.value = enabled
    }

    /** The next [failures] reads of the [kind] sensor will fail */
    fun inject(kind: SensorKind, failures: Int = DEFAULT_FAILURES) {
        pendingFailures.getOrPut(kind) { AtomicInteger(0) }.set(failures)
    }

    /** Called before every read; returns the fault type, or null if everything is fine */
    fun nextFault(kind: SensorKind): FaultType? {
        val pending = pendingFailures[kind]
        if (pending != null && pending.get() > 0) {
            pending.decrementAndGet()
            return FaultType.NO_SIGNAL
        }
        if (_randomFaults.value && random.nextDouble() < RANDOM_FAULT_PROBABILITY) {
            return if (random.nextBoolean()) FaultType.NO_SIGNAL else FaultType.CORRUPTED_VALUE
        }
        return null
    }

    companion object {
        const val DEFAULT_FAILURES = 3
        const val RANDOM_FAULT_PROBABILITY = 0.12
    }
}