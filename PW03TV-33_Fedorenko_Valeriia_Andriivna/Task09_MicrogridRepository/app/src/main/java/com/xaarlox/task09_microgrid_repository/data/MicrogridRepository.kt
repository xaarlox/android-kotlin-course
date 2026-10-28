package com.xaarlox.task09_microgrid_repository.data

import com.xaarlox.task09_microgrid_repository.data.local.PowerDao
import com.xaarlox.task09_microgrid_repository.data.local.PowerEntity
import com.xaarlox.task09_microgrid_repository.data.mapper.toDomain
import com.xaarlox.task09_microgrid_repository.data.mapper.toEntity
import com.xaarlox.task09_microgrid_repository.data.remote.ForecastRemoteDataSource
import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import com.xaarlox.task09_microgrid_repository.domain.PowerStats
import com.xaarlox.task09_microgrid_repository.domain.RecordSource
import com.xaarlox.task09_microgrid_repository.util.retryWithBackoff
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository: the single place that knows about ALL data sources:
 * 1. Room (local, "source of truth" for the UI);
 * 2. Remote forecast service (ForecastRemoteDataSource);
 * 3. In-memory cache of the service response (ForecastCache).
 *
 * Architectural decisions:
 * - The UI observes ONLY Room via Flow (single source of truth): network data
 * reaches the UI by being written to the DB, allowing the screen to work offline.
 * - The repository merely coordinates sources. Model mapping, statistics calculation, and
 * request retries are moved to separate files (mapper, PowerStats, Retry).
 * - Errors are not thrown directly to the UI, but returned as Result.
 */
class MicrogridRepository(
    private val dao: PowerDao,
    private val remote: ForecastRemoteDataSource,
    private val cache: ForecastCache = ForecastCache(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val records: Flow<List<PowerRecord>> =
        dao.observeAll()
            .map { list -> list.map(PowerEntity::toDomain) }
            .flowOn(ioDispatcher)

    val stats: Flow<PowerStats> =
        records.map(PowerStats::from)
            .flowOn(Dispatchers.Default)

    private val syncMutex = Mutex()

    /**
     * Loads forecasts: first from a fresh cache, otherwise from the network (with retries)
     *
     * The result is written to Room
     *
     * @return Result: true if data is from cache, false if from network
     */
    suspend fun refreshForecasts(): Result<Boolean> = syncMutex.withLock {
        try {
            val cached = cache.getFresh()
            val forecast = cached ?: withContext(ioDispatcher) {
                retryWithBackoff(times = 3) { remote.fetchForecast() }
            }.also(cache::put)

            withContext(ioDispatcher) {
                dao.replaceBySource(
                    RecordSource.FORECAST.name,
                    forecast.map { it.toEntity() }
                )
            }
            Result.success(cached != null)
        } catch (e: CancellationException) {
            throw e // скасування корутини не помилка, пропускаємо далі
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRecord(day: Int, powerKw: Double): Result<Unit> {
        if (day !in MIN_DAY..MAX_DAY) {
            return Result.failure(IllegalArgumentException("День має бути від $MIN_DAY до $MAX_DAY"))
        }
        if (!powerKw.isFinite() || powerKw <= 0.0 || powerKw > MAX_POWER_KW) {
            return Result.failure(IllegalArgumentException("Потужність має бути від 0 до ${MAX_POWER_KW.toInt()} кВт"))
        }
        return try {
            withContext(ioDispatcher) {
                dao.insert(
                    PowerEntity(
                        day = day,
                        powerKw = powerKw,
                        source = RecordSource.MANUAL.name
                    )
                )
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecord(id: Long) {
        withContext(ioDispatcher) { dao.deleteById(id) }
    }

    private companion object {
        const val MIN_DAY = 1
        const val MAX_DAY = 31
        const val MAX_POWER_KW = 100_000.0
    }
}