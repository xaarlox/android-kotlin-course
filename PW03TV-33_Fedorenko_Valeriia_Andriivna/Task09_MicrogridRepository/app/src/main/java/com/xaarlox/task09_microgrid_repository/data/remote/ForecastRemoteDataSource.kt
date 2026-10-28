package com.xaarlox.task09_microgrid_repository.data.remote

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * The second data source: a remote forecast service
 *
 * We simulate network activity with delays and random failures ([failureRate]) to demonstrate
 * error handling and retries in the Repository during practice
 *
 * For a real API, simply replace the body of fetchForecast() with a Retrofit call —
 * the rest of the code will remain unchanged, as the Repository depends solely on this class
 */
class ForecastRemoteDataSource(private val failureRate: Double = 0.25) {

    suspend fun fetchForecast(): List<ForecastDto> {
        delay(800.milliseconds)
        if (Random.nextDouble() < failureRate) {
            throw IOException("Сервер прогнозів тимчасово недоступний")
        }
        return listOf(
            ForecastDto(day = 1, powerKw = 100.0),
            ForecastDto(day = 2, powerKw = 200.0),
            ForecastDto(day = 3, powerKw = 300.0)
        )
    }
}