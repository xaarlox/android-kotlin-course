package com.xaarlox.task13_microgrid_flow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task13_microgrid_flow.data.FaultController
import com.xaarlox.task13_microgrid_flow.data.MicrogridRepository
import com.xaarlox.task13_microgrid_flow.data.MicrogridSimulator
import com.xaarlox.task13_microgrid_flow.domain.ForecastAccuracy
import com.xaarlox.task13_microgrid_flow.domain.HistoryPoint
import com.xaarlox.task13_microgrid_flow.domain.MicrogridSnapshot
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

/**
 * The ViewModel holds the whole screen state. The hot sensor flows live in [viewModelScope],
 * so data is not lost on screen rotation and stops only when the ViewModel is destroyed
 */
class MicrogridViewModel : ViewModel() {

    private val faults = FaultController()

    // Dispatchers.Default — value generation does not load the main thread
    private val simulator = MicrogridSimulator(
        scope = viewModelScope + Dispatchers.Default,
        faults = faults
    ).also { it.start() }

    private val repository = MicrogridRepository(simulator)

    /** Latest snapshot + chart history (scan accumulates state between flow values) */
    private data class Telemetry(
        val latest: MicrogridSnapshot? = null,
        val history: List<HistoryPoint> = emptyList()
    )

    private val telemetry: Flow<Telemetry> = repository.snapshots
        .scan(Telemetry()) { acc, snapshot ->
            Telemetry(
                latest = snapshot,
                history = (acc.history + HistoryPoint(snapshot.generationKw, snapshot.loadKw))
                    .takeLast(HISTORY_SIZE)
            )
        }

    // Until the first "actual — forecast" pair arrives we show null (waiting state)
    private val accuracy: Flow<ForecastAccuracy?> = repository.forecastAccuracy
        .onStart<ForecastAccuracy?> { emit(null) }

    /** Combines five flows (snapshot, forecast, statuses, log, toggle) into one UI state */
    val uiState: StateFlow<MicrogridUiState> = combine(
        telemetry,
        accuracy,
        simulator.statuses,
        simulator.events,
        faults.randomFaults
    ) { data, forecast, statuses, events, randomFaults ->
        MicrogridUiState(
            snapshot = data.latest,
            forecastAccuracy = forecast,
            statuses = statuses,
            history = data.history,
            events = events,
            randomFaultsEnabled = randomFaults
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MicrogridUiState()
    )

    /** The "Збій" (Fault) button: the next few reads of the sensor will fail */
    fun injectFault(kind: SensorKind) = faults.inject(kind)

    fun setRandomFaults(enabled: Boolean) = faults.setRandomFaults(enabled)

    private companion object {
        const val HISTORY_SIZE = 40
    }
}