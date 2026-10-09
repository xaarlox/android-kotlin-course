package com.xaarlox.task08_microgrid_room.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.xaarlox.task08_microgrid_room.data.local.AppDatabase
import com.xaarlox.task08_microgrid_room.data.remote.FakeRemoteStationDataSource
import com.xaarlox.task08_microgrid_room.data.repository.AddOutcome
import com.xaarlox.task08_microgrid_room.data.repository.StationRepository
import com.xaarlox.task08_microgrid_room.data.repository.SyncOutcome
import com.xaarlox.task08_microgrid_room.domain.GridStats
import com.xaarlox.task08_microgrid_room.domain.InputError
import com.xaarlox.task08_microgrid_room.domain.InputField
import com.xaarlox.task08_microgrid_room.domain.ParseResult
import com.xaarlox.task08_microgrid_room.domain.PowerFilter
import com.xaarlox.task08_microgrid_room.domain.SortOrder
import com.xaarlox.task08_microgrid_room.domain.Station
import com.xaarlox.task08_microgrid_room.domain.StationAnalytics
import com.xaarlox.task08_microgrid_room.domain.StationInputParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** State of the add station form */
data class FormState(
    val name: String = "",
    val powers: String = "",
    val voltages: String = "",
    val errors: Map<InputField, InputError> = emptyMap(),
    val isSaving: Boolean = false
)

/** State of the station list after filtering and sorting */
data class StationListState(
    val items: List<Station> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true
)

/** Server synchronization state exposed to the UI */
sealed interface SyncUiState {
    object Idle : SyncUiState
    object Syncing : SyncUiState
    object UpToDate : SyncUiState
    data class Synced(val added: Int) : SyncUiState
    object Failed : SyncUiState
}

/**
 * MVVM: ViewModel. Single responsibility: transform Repository data into UI state
 * and handle user actions. It has no direct awareness of Room or network operations
 * (interacting exclusively through the Repository)
 *
 * Architectural decisions regarding state:
 *  - Database data and derived lists use StateFlow (a stream of updates emitted by Room);
 *  - Text field input uses Compose State to update synchronously and instantaneously
 *    (avoiding coroutine dispatch latency), which prevents cursor jumping;
 *  - Filter, sort order, and search query are merged with the station stream via combine,
 *    automatically recomputing the list whenever any of these inputs change.
 */
class StationViewModel(private val repository: StationRepository) : ViewModel() {
    var form by mutableStateOf(FormState())
        private set

    var query by mutableStateOf("")
        private set
    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow(PowerFilter.ALL)
    private val _sort = MutableStateFlow(SortOrder.NAME)

    val filter: StateFlow<PowerFilter> = _filter.asStateFlow()
    val sort: StateFlow<SortOrder> = _sort.asStateFlow()

    val listState: StateFlow<StationListState> = combine(
        repository.stations, _query, _filter, _sort
    ) { all, q, f, s ->
        StationListState(
            items = StationAnalytics.filterAndSort(all, q, f, s),
            totalCount = all.size,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StationListState())

    val gridStats: StateFlow<GridStats> = repository.gridStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GridStats.EMPTY)

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    init {
        sync()
    }

    /** Synchronizes data with the server; [force] bypasses the cache (e.g., triggered by the "Sync" button) */
    fun sync(force: Boolean = false) {
        if (_syncState.value == SyncUiState.Syncing) return
        _syncState.value = SyncUiState.Syncing
        viewModelScope.launch {
            _syncState.value = when (val outcome = repository.syncFromRemote(force)) {
                SyncOutcome.UpToDate -> SyncUiState.UpToDate
                is SyncOutcome.Synced -> SyncUiState.Synced(outcome.added)
                is SyncOutcome.Failed -> SyncUiState.Failed
            }
        }
    }

    fun onNameChange(value: String) {
        form = form.copy(name = value, errors = form.errors - InputField.NAME)
    }

    fun onPowersChange(value: String) {
        form = form.copy(powers = value, errors = form.errors - InputField.POWERS)
    }

    fun onVoltagesChange(value: String) {
        form = form.copy(voltages = value, errors = form.errors - InputField.VOLTAGES)
    }

    /** Validates the form; on success, persists the station via the Repository */
    fun onAddStation() {
        val current = form
        if (current.isSaving) return
        when (val parsed =
            StationInputParser.parse(current.name, current.powers, current.voltages)) {
            is ParseResult.Invalid -> form = current.copy(errors = parsed.errors)
            is ParseResult.Valid -> {
                form = current.copy(errors = emptyMap(), isSaving = true)
                viewModelScope.launch {
                    when (repository.addStation(parsed.draft)) {
                        AddOutcome.ADDED -> form = FormState()
                        AddOutcome.DUPLICATE_NAME -> form = form.copy(
                            isSaving = false,
                            errors = mapOf(InputField.NAME to InputError.DUPLICATE_NAME)
                        )
                    }
                }
            }
        }
    }

    fun onQueryChange(value: String) {
        query = value
        _query.value = value
    }

    fun onFilterChange(value: PowerFilter) {
        _filter.value = value
    }

    fun onSortChange(value: SortOrder) {
        _sort.value = value
    }

    fun deleteStation(id: Long) {
        viewModelScope.launch { repository.deleteStation(id) }
    }

    companion object {
        /**
         * ViewModel factory: constructs the dependency graph
         * (Database → DAO → Repository → ViewModel). In production-scale projects, this is handled by a DI framework (Hilt/Koin).
         */
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val database = AppDatabase.get(context)
                StationViewModel(
                    StationRepository(database.stationDao(), FakeRemoteStationDataSource())
                )
            }
        }
    }
}