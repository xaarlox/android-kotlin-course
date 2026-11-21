package com.xaarlox.task14_microgrid_map_list.ui

import androidx.lifecycle.ViewModel
import com.xaarlox.task14_microgrid_map_list.data.EnergyObjectsRepository
import com.xaarlox.task14_microgrid_map_list.domain.EnergyObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Screen state: the collection of objects and the selected id (shared by the list and the map) */
data class MapListUiState(
    val objects: List<EnergyObject> = emptyList(),
    val selectedId: String? = null
) {
    val selected: EnergyObject? get() = objects.firstOrNull { it.id == selectedId }
}

/**
 * The single source of truth about the selection. The list and the map do not know about each other:
 * both only read [uiState] and report taps to the ViewModel.
 * This is exactly what provides the two-way synchronization
 */
class MapListViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(MapListUiState(objects = EnergyObjectsRepository.objects))
    val uiState: StateFlow<MapListUiState> = _uiState.asStateFlow()

    /** Tap on a card: select it; tapping the selected card again clears the selection */
    fun onCardClick(id: String) = _uiState.update {
        it.copy(selectedId = if (it.selectedId == id) null else id)
    }

    /** Tap on a map marker: select it (the list will scroll to the card) */
    fun onMarkerClick(id: String) = _uiState.update { it.copy(selectedId = id) }

    /** Tap on an empty spot of the map: clear the selection, the camera returns to the overview */
    fun onMapClick() = _uiState.update { it.copy(selectedId = null) }
}