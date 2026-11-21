package com.xaarlox.task15_microgrid_filters.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task15_microgrid_filters.data.EnergyObjectsRepository
import com.xaarlox.task15_microgrid_filters.domain.EnergyObject
import com.xaarlox.task15_microgrid_filters.domain.EnergyType
import com.xaarlox.task15_microgrid_filters.domain.ListCriteria
import com.xaarlox.task15_microgrid_filters.domain.ObjectStatus
import com.xaarlox.task15_microgrid_filters.domain.PowerClass
import com.xaarlox.task15_microgrid_filters.domain.SelectionSummary
import com.xaarlox.task15_microgrid_filters.domain.SortField
import com.xaarlox.task15_microgrid_filters.domain.applyCriteria
import com.xaarlox.task15_microgrid_filters.domain.summarize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Everything the screen needs: the resulting list, counters and the selection */
data class ObjectsUiState(
    val criteria: ListCriteria = ListCriteria(),
    val visible: List<EnergyObject> = emptyList(),
    val totalCount: Int = 0,
    val selectedIds: Set<String> = emptySet(),
    val selection: SelectionSummary = SelectionSummary()
)

class ObjectsViewModel : ViewModel() {

    private val allObjects: List<EnergyObject> = EnergyObjectsRepository.objects

    private val _criteria = MutableStateFlow(ListCriteria())
    val criteria: StateFlow<ListCriteria> = _criteria.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    val uiState: StateFlow<ObjectsUiState> =
        combine(_criteria, _selectedIds) { criteria, selected ->
            buildState(criteria, selected)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(ListCriteria(), emptySet())
        )

    private fun buildState(criteria: ListCriteria, selected: Set<String>) = ObjectsUiState(
        criteria = criteria,
        visible = allObjects.applyCriteria(criteria),
        totalCount = allObjects.size,
        selectedIds = selected,
        // The summary is calculated over ALL selected items, even if the filter hides them right now
        selection = allObjects.filter { it.id in selected }.summarize()
    )

    fun onQueryChange(query: String) = _criteria.update { it.copy(query = query) }

    fun toggleType(type: EnergyType) = _criteria.update { it.copy(types = it.types.toggled(type)) }

    fun toggleStatus(status: ObjectStatus) =
        _criteria.update { it.copy(statuses = it.statuses.toggled(status)) }

    fun togglePowerClass(powerClass: PowerClass) =
        _criteria.update { it.copy(powerClasses = it.powerClasses.toggled(powerClass)) }

    /** Tap on a sort chip: another field — select it; the same field — flip the direction */
    fun onSortClick(field: SortField) = _criteria.update {
        if (it.sortField == field) it.copy(descending = !it.descending)
        else it.copy(sortField = field, descending = false)
    }

    fun resetFilters() = _criteria.update { it.withoutFilters() }

    fun toggleSelection(id: String) = _selectedIds.update { it.toggled(id) }

    /** "Усі показані" (All shown): adds to the selection everything that passes the current filters */
    fun selectAllVisible() {
        val visibleIds = allObjects.applyCriteria(_criteria.value).map { it.id }
        _selectedIds.update { it + visibleIds }
    }

    fun clearSelection() = _selectedIds.update { emptySet() }

    private fun <T> Set<T>.toggled(item: T): Set<T> = if (item in this) this - item else this + item
}