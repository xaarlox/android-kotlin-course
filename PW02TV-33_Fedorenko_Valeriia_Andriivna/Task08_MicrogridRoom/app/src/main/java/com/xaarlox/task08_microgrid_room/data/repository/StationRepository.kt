package com.xaarlox.task08_microgrid_room.data.repository

import com.xaarlox.task08_microgrid_room.data.local.ReadingKind
import com.xaarlox.task08_microgrid_room.data.local.StationDao
import com.xaarlox.task08_microgrid_room.data.local.StationEntity
import com.xaarlox.task08_microgrid_room.data.local.StationWithReadings
import com.xaarlox.task08_microgrid_room.data.remote.RemoteStationDataSource
import com.xaarlox.task08_microgrid_room.domain.GridStats
import com.xaarlox.task08_microgrid_room.domain.Station
import com.xaarlox.task08_microgrid_room.domain.StationAnalytics
import com.xaarlox.task08_microgrid_room.domain.StationDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository: the single entry point for data access for the ViewModel
 *
 * Architectural decisions:
 *  1. The local Room database acts as the single source of truth (SSOT). The UI reads exclusively
 *     from it; remote data is cached into Room first before propagating to the UI.
 *     This ensures offline support and prevents UI flickering between different data sources
 *  2. Caching: synchronization with the server occurs no more frequently than once every [CACHE_TTL_MS],
 *     unless a manual refresh is requested (force)
 *  3. Error handling: network failures do not reach the UI as unhandled exceptions; they are wrapped into
 *     [SyncOutcome.Failed], keeping local data accessible
 *  4. Business logic (name uniqueness checks, metrics aggregation) resides here and in the domain layer,
 *     never in the UI or ViewModel
 *  5. Asynchronous operations: all tasks use suspend functions/Flow and run within coroutines
 *
 * @param local Local data source (Room DAO)
 * @param remote Remote data source
 * @param clock Time provider (can be mocked/substituted in tests)
 */
sealed interface SyncOutcome {
    object UpToDate : SyncOutcome
    data class Synced(val added: Int) : SyncOutcome
    data class Failed(val cause: Exception) : SyncOutcome
}

enum class AddOutcome { ADDED, DUPLICATE_NAME }


class StationRepository(
    private val local: StationDao,
    private val remote: RemoteStationDataSource,
    private val clock: () -> Long = System::currentTimeMillis
) {
    private var lastSyncAt = 0L

    val stations: Flow<List<Station>> =
        local.observeStations().map { list -> list.map { it.toDomain() } }

    val gridStats: Flow<GridStats> = stations.map(StationAnalytics::aggregate)

    /**
     * Fetches stations from the remote server into the local database (respecting cache policy)
     * Stations with existing names are skipped to prevent duplicates
     *
     * @param force If true, bypasses the cache to perform a manual refresh
     */
    suspend fun syncFromRemote(force: Boolean = false): SyncOutcome {
        if (!force && clock() - lastSyncAt < CACHE_TTL_MS) return SyncOutcome.UpToDate
        return try {
            val remoteStations = remote.fetchStations()
            val knownNames = local.getAllNames().map { it.lowercase() }.toMutableSet()
            var added = 0
            for (dto in remoteStations) {
                if (knownNames.add(dto.name.lowercase())) {
                    local.insertWithReadings(
                        StationEntity(name = dto.name, createdAt = clock()),
                        dto.powers,
                        dto.voltages
                    )
                    added++
                }
            }
            lastSyncAt = clock()
            SyncOutcome.Synced(added)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            SyncOutcome.Failed(e)
        }
    }

    suspend fun addStation(draft: StationDraft): AddOutcome {
        val exists = local.getAllNames().any { it.equals(draft.name, ignoreCase = true) }
        if (exists) return AddOutcome.DUPLICATE_NAME
        local.insertWithReadings(
            StationEntity(name = draft.name, createdAt = clock()),
            draft.powers,
            draft.voltages
        )
        return AddOutcome.ADDED
    }

    suspend fun deleteStation(id: Long) = local.deleteById(id)

    private companion object {
        const val CACHE_TTL_MS = 60_000L
    }
}

/** Maps a Room entity model to a domain model, preserving the order of measurements by id */
private fun StationWithReadings.toDomain(): Station {
    val sorted = readings.sortedBy { it.id }
    return Station(
        id = station.id,
        name = station.name,
        powers = sorted.filter { it.kind == ReadingKind.POWER }.map { it.value },
        voltages = sorted.filter { it.kind == ReadingKind.VOLTAGE }.map { it.value },
        createdAt = station.createdAt
    )
}