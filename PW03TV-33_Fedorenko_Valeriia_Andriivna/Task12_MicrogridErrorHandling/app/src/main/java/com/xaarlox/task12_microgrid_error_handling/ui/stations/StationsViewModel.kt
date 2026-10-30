package com.xaarlox.task12_microgrid_error_handling.ui.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task12_microgrid_error_handling.data.StationsRepository
import com.xaarlox.task12_microgrid_error_handling.data.network.ApiResult
import com.xaarlox.task12_microgrid_error_handling.data.network.AppError
import com.xaarlox.task12_microgrid_error_handling.data.network.ConnectivityChecker
import com.xaarlox.task12_microgrid_error_handling.data.network.RetryInfo
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultScenario
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultSimulator
import com.xaarlox.task12_microgrid_error_handling.ui.UiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The ViewModel is unaware of Retrofit, Gson, or exceptions: the repository returns an ApiResult with a prepared AppError.
 * Here, only "how to display" decisions are made:
 *   - 401/403 → dialog (isBlocking);
 *   - stale data exists → Snackbar + "showing cached data" banner;
 *   - no data available → inline error state with a "Retry" button;
 *   - action error (POST) → Snackbar with a manual "Retry" option
 */
class StationsViewModel(
    private val repository: StationsRepository,
    private val connectivity: ConnectivityChecker,
    private val simulator: FaultSimulator
) : ViewModel() {

    private data class Local(
        val isLoading: Boolean = false,
        val retry: RetryInfo? = null,
        val failure: FailureInfo? = null,
        val dialog: AppError? = null,
        val busyStationId: Int? = null
    )

    private val local = MutableStateFlow(Local())
    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val uiState: StateFlow<StationsUiState> = combine(
        repository.stations,
        repository.lastUpdated,
        connectivity.online,
        simulator.scenario,
        local
    ) { stations, updated, online, scenario, l ->
        StationsUiState(
            stations, updated, online, scenario,
            l.isLoading, l.retry, l.failure, l.dialog, l.busyStationId
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StationsUiState())

    init {
        refresh()
        viewModelScope.launch {
            connectivity.online.collect { online ->
                if (online && local.value.failure?.error is AppError.NoConnection) refresh()
            }
        }
    }

    fun refresh() {
        if (local.value.isLoading) return
        viewModelScope.launch {
            local.update { it.copy(isLoading = true, retry = null, failure = null) }

            when (val result =
                repository.refresh { info -> local.update { it.copy(retry = info) } }) {
                is ApiResult.Success -> {
                    if (result.attempts > 1) {
                        _messages.trySend(UiMessage("Дані оновлено з ${result.attempts}-ї спроби"))
                    }
                }

                is ApiResult.Failure -> {
                    val error = result.error
                    local.update {
                        it.copy(
                            failure = FailureInfo(error, result.attempts),
                            dialog = error.takeIf { e -> e.isBlocking }
                        )
                    }
                    if (repository.stations.value.isNotEmpty() && !error.isBlocking) {
                        _messages.trySend(
                            UiMessage(
                                error.message,
                                this@StationsViewModel::refresh.takeIf { error.retryableByUser })
                        )
                    }
                }
            }
            local.update { it.copy(isLoading = false, retry = null) }
        }
    }

    fun toggleStation(id: Int) {
        if (local.value.busyStationId != null) return
        viewModelScope.launch {
            local.update { it.copy(busyStationId = id) }
            val result = repository.toggle(id)
            if (result is ApiResult.Failure) {
                val error = result.error
                if (error.isBlocking) {
                    local.update { it.copy(dialog = error) }
                } else {
                    _messages.trySend(
                        UiMessage(
                            error.message,
                            retry = { toggleStation(id) }.takeIf { error.retryableByUser })
                    )
                }
            }
            local.update { it.copy(busyStationId = null) }
        }
    }

    fun selectScenario(scenario: FaultScenario) = simulator.select(scenario)

    fun dismissDialog() = local.update { it.copy(dialog = null) }
}