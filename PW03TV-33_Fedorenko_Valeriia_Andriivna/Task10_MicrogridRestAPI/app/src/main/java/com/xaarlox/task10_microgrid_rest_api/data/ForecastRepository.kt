package com.xaarlox.task10_microgrid_rest_api.data

import com.xaarlox.task10_microgrid_rest_api.data.local.ForecastDao
import com.xaarlox.task10_microgrid_rest_api.data.local.ForecastEntity
import com.xaarlox.task10_microgrid_rest_api.data.mapper.toDomain
import com.xaarlox.task10_microgrid_rest_api.data.mapper.toEntities
import com.xaarlox.task10_microgrid_rest_api.data.remote.CityNotFoundException
import com.xaarlox.task10_microgrid_rest_api.data.remote.OpenMeteoApi
import com.xaarlox.task10_microgrid_rest_api.domain.DayForecast
import com.xaarlox.task10_microgrid_rest_api.domain.ForecastSummary
import com.xaarlox.task10_microgrid_rest_api.domain.StationConfig
import com.xaarlox.task10_microgrid_rest_api.util.retryWithBackoff
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interaction between Retrofit, Gson, and ViewModel:
 *  - Retrofit executes HTTP requests, Gson converts JSON into DTOs (data/remote layer);
 *  - Repository validates DTOs, maps them into Room entities, and writes them to the DB;
 *  - ViewModel subscribes ONLY to Room via Flow: the network updates the UI indirectly,
 *    allowing the screen to work offline while showing the latest saved data (single source of truth);
 *  - Errors are returned as Result rather than thrown to the UI
 */
class ForecastRepository(
    private val dao: ForecastDao,
    private val api: OpenMeteoApi,
    private val config: StationConfig = StationConfig(),
    private val cache: TtlCache<String, List<ForecastEntity>> = TtlCache(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val forecast: Flow<List<DayForecast>> =
        dao.observeAll()
            .map { list -> list.map { it.toDomain(config) } }
            .flowOn(ioDispatcher)

    val summary: Flow<ForecastSummary> =
        forecast.map(ForecastSummary::from).flowOn(Dispatchers.Default)

    val location: Flow<String?> =
        dao.observeAll().map { it.firstOrNull()?.location }.flowOn(ioDispatcher)

    private val syncMutex = Mutex()

    /** @return Result<Boolean>: true: data from cache, false: from network */
    suspend fun refresh(city: String): Result<Boolean> = syncMutex.withLock {
        try {
            val name = city.trim()
            val key = name.lowercase()
            val cached = cache.getFresh(key)
            val entities = cached ?: withContext(ioDispatcher) { fetchFromNetwork(name) }
                .also { cache.put(key, it) }

            withContext(ioDispatcher) { dao.replaceAll(entities) }
            Result.success(cached != null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Two sequential requests: the second depends on the result of the first (city coordinates) */
    private suspend fun fetchFromNetwork(city: String): List<ForecastEntity> {
        val place = retryWithBackoff(times = 3, shouldRetry = ::isTransient) {
            api.searchCity(city)
        }.results?.firstOrNull() ?: throw CityNotFoundException(city)

        val response = retryWithBackoff(times = 3, shouldRetry = ::isTransient) {
            api.getForecast(place.latitude, place.longitude)
        }
        val title = listOfNotNull(place.name, place.country).joinToString(", ")
        return response.toEntities(title.ifBlank { city })
    }

    /** Retries network failures and 5xx errors. Retrying 4xx errors and malformed JSON is pointless */
    private fun isTransient(e: Throwable) =
        e is IOException || (e is HttpException && e.code() >= 500)
}