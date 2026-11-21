package com.xaarlox.task16_microgrid_gps_map.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task16_microgrid_gps_map.data.EnergyObjectsRepository
import com.xaarlox.task16_microgrid_gps_map.data.FusedLocationSource
import com.xaarlox.task16_microgrid_gps_map.data.LocationSource
import com.xaarlox.task16_microgrid_gps_map.domain.EnergyObject
import com.xaarlox.task16_microgrid_gps_map.domain.EnergyType
import com.xaarlox.task16_microgrid_gps_map.domain.GeoPoint
import com.xaarlox.task16_microgrid_gps_map.domain.ListCriteria
import com.xaarlox.task16_microgrid_gps_map.domain.LocationState
import com.xaarlox.task16_microgrid_gps_map.domain.ObjectStatus
import com.xaarlox.task16_microgrid_gps_map.domain.PowerClass
import com.xaarlox.task16_microgrid_gps_map.domain.SortField
import com.xaarlox.task16_microgrid_gps_map.domain.applyCriteria
import com.xaarlox.task16_microgrid_gps_map.domain.distanceKmFrom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** The full state of the "list + map + GPS" screen */
data class GpsMapUiState(
    val criteria: ListCriteria = ListCriteria(),
    val visible: List<EnergyObject> = emptyList(),
    val totalCount: Int = 0,
    /** The selected object; always one of [visible] (if a filter hides it, the selection is cleared) */
    val selectedId: String? = null,
    val location: LocationState = LocationState.NoPermission,
    /** Distance from the user to every visible object, km (empty until there is GPS) */
    val distancesKm: Map<String, Double> = emptyMap()
) {
    val userPoint: GeoPoint? get() = (location as? LocationState.Available)?.point
    val selected: EnergyObject? get() = visible.firstOrNull { it.id == selectedId }
}

@OptIn(ExperimentalCoroutinesApi::class)
class GpsMapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationSource: LocationSource = FusedLocationSource(application)
    private val allObjects: List<EnergyObject> = EnergyObjectsRepository.objects

    private val permissionGranted = MutableStateFlow(false)
    private val criteria = MutableStateFlow(ListCriteria())
    private val selectedId = MutableStateFlow<String?>(null)

    /**
     * GPS is turned on only when there is a permission. If the permission disappears, flatMapLatest cancels
     * the previous flow (FusedLocationProviderClient stops sending updates)
     */
    private val locationState: Flow<LocationState> = permissionGranted.flatMapLatest { granted ->
        if (!granted) {
            flowOf(LocationState.NoPermission)
        } else {
            locationSource.updates()
                .map<GeoPoint, LocationState> { LocationState.Available(it) }
                .onStart { emit(LocationState.Searching) }
                .catch { emit(LocationState.Error(it.message ?: "Не вдалося отримати позицію")) }
        }
    }

    val uiState: StateFlow<GpsMapUiState> =
        combine(criteria, selectedId, locationState) { crit, selected, location ->
            val user = (location as? LocationState.Available)?.point
            val visible = allObjects.applyCriteria(crit, user)
            GpsMapUiState(
                criteria = crit,
                visible = visible,
                totalCount = allObjects.size,
                // Objects hidden by a filter cannot be selected
                selectedId = selected?.takeIf { id -> visible.any { it.id == id } },
                location = location,
                distancesKm = if (user == null) emptyMap()
                else visible.associate { it.id to it.distanceKmFrom(user) }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GpsMapUiState(visible = allObjects, totalCount = allObjects.size)
        )

    /** The screen reports a change of the permission state (the user answered the request or changed it in settings) */
    fun onPermissionChanged(granted: Boolean) {
        permissionGranted.value = granted
    }

    fun onQueryChange(query: String) = criteria.update { it.copy(query = query) }

    fun toggleType(type: EnergyType) = criteria.update { it.copy(types = it.types.toggled(type)) }

    fun toggleStatus(status: ObjectStatus) =
        criteria.update { it.copy(statuses = it.statuses.toggled(status)) }

    fun togglePowerClass(powerClass: PowerClass) =
        criteria.update { it.copy(powerClasses = it.powerClasses.toggled(powerClass)) }

    fun onSortClick(field: SortField) = criteria.update {
        if (it.sortField == field) it.copy(descending = !it.descending)
        else it.copy(sortField = field, descending = false)
    }

    fun resetFilters() = criteria.update { it.withoutFilters() }

    fun onCardClick(id: String) = selectedId.update { if (it == id) null else id }

    fun onMarkerClick(id: String) = selectedId.update { id }

    fun onMapClick() = selectedId.update { null }

    private fun <T> Set<T>.toggled(item: T): Set<T> = if (item in this) this - item else this + item
}