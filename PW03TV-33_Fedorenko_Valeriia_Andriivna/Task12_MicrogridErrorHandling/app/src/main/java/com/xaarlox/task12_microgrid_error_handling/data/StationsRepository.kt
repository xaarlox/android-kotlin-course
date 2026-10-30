package com.xaarlox.task12_microgrid_error_handling.data

import com.xaarlox.task12_microgrid_error_handling.data.mapper.toDomain
import com.xaarlox.task12_microgrid_error_handling.data.network.ApiResult
import com.xaarlox.task12_microgrid_error_handling.data.network.InvalidDataException
import com.xaarlox.task12_microgrid_error_handling.data.network.NetworkExecutor
import com.xaarlox.task12_microgrid_error_handling.data.network.RetryInfo
import com.xaarlox.task12_microgrid_error_handling.data.remote.MicrogridApi
import com.xaarlox.task12_microgrid_error_handling.domain.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The latest successful snapshot is kept in memory: if an update fails, the user continues
 * to see stale data with an indicator rather than a blank screen
 */
class StationsRepository(
    private val api: MicrogridApi,
    private val executor: NetworkExecutor
) {
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()

    private val _lastUpdated = MutableStateFlow<Long?>(null)
    val lastUpdated: StateFlow<Long?> = _lastUpdated.asStateFlow()

    /** GET is idempotent, so it is safely retried automatically */
    suspend fun refresh(onRetry: (RetryInfo) -> Unit): ApiResult<List<Station>> {
        val result = executor.execute("GET /api/stations", idempotent = true, onRetry = onRetry) {
            val dtos = api.getStations()
            val mapped = dtos.mapNotNull { it.toDomain() }
            if (dtos.isNotEmpty() && mapped.isEmpty()) throw InvalidDataException()
            mapped
        }
        if (result is ApiResult.Success) {
            _stations.value = result.data
            _lastUpdated.value = System.currentTimeMillis()
        }
        return result
    }

    /** POST is state-modifying, so auto-retry is disabled (idempotent = false) */
    suspend fun toggle(id: Int): ApiResult<Station> {
        val result = executor.execute("POST /api/stations/$id/toggle", idempotent = false) {
            api.toggleStation(id).toDomain() ?: throw InvalidDataException()
        }
        if (result is ApiResult.Success) {
            val updated = result.data
            _stations.update { list -> list.map { if (it.id == updated.id) updated else it } }
        }
        return result
    }
}