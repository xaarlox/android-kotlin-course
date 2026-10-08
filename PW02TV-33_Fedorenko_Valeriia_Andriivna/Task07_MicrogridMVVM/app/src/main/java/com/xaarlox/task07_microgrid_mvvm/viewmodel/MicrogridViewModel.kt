package com.xaarlox.task07_microgrid_mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task07_microgrid_mvvm.domain.EnergySource
import com.xaarlox.task07_microgrid_mvvm.domain.FakePowerRepository
import com.xaarlox.task07_microgrid_mvvm.domain.PowerClassifier
import com.xaarlox.task07_microgrid_mvvm.domain.PowerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVVM: ViewModel layer
 *
 * What the ViewModel does in this architecture:
 *  - holds the screen state ([states], [summary]) and survives screen rotation;
 *  - launches asynchronous work via coroutines (viewModelScope);
 *  - invokes business logic (Model) and maps its result to the UI state;
 *  - knows nothing about Compose: the UI simply subscribes to the StateFlow and calls [refresh] methods
 */
class MicrogridViewModel : ViewModel() {
    private val repository: PowerRepository = FakePowerRepository()

    private val _states = MutableStateFlow<Map<EnergySource, PowerUiState>>(
        EnergySource.entries.associateWith { PowerUiState.Loading }
    )

    val states: StateFlow<Map<EnergySource, PowerUiState>> = _states.asStateFlow()

    /**
     * Derived microgrid state: automatically recalculated when any of the [states] change
     * (interaction between multiple state flows)
     */
    val summary: StateFlow<SummaryState> = _states
        .map(::summarize)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = summarize(_states.value)
        )

    init {
        EnergySource.entries.forEach(::load)
    }

    fun refresh(source: EnergySource) {
        if (_states.value[source] == PowerUiState.Loading) return
        load(source)
    }

    fun refreshAll() {
        EnergySource.entries.forEach(::refresh)
    }

    private fun load(source: EnergySource) {
        _states.update { it + (source to PowerUiState.Loading) }
        viewModelScope.launch {
            val newState = repository.fetchPower(source).fold(
                onSuccess = { kw ->
                    PowerUiState.Success(kw, PowerClassifier.classify(kw, source.capacityKw))
                },
                onFailure = { PowerUiState.Error }
            )
            _states.update { it + (source to newState) }
        }
    }

    /** Calculates the overall microgrid state from the states of individual sources */
    private fun summarize(states: Map<EnergySource, PowerUiState>): SummaryState {
        val values = states.values
        if (values.any { it is PowerUiState.Loading }) return SummaryState.Pending

        val successes = values.filterIsInstance<PowerUiState.Success>()
        val total = successes.sumOf { it.powerKw }
        return when {
            successes.isEmpty() -> SummaryState.Unavailable
            successes.size < values.size -> SummaryState.Partial(total)
            else -> SummaryState.Ready(
                totalKw = total,
                status = PowerClassifier.classify(
                    total,
                    EnergySource.entries.sumOf { it.capacityKw })
            )
        }
    }
}