package com.xaarlox.task10_microgrid_rest_api.data.remote

import com.xaarlox.task10_microgrid_rest_api.data.remote.dto.ForecastResponse
import com.xaarlox.task10_microgrid_rest_api.data.remote.dto.GeoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit converts this interface into an HTTP client: annotations describe the request,
 * and GsonConverterFactory automatically parses the JSON into DTOs. Since the methods are suspend,
 * the calls do not block the main thread, and errors are thrown as exceptions
 * (IOException for network issues, HttpException for 4xx/5xx codes)
 */
interface OpenMeteoApi {

    /** Endpoint #1 (different host, hence the full URL instead of the base URL) */
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1,
        @Query("language") language: String = "uk"
    ): GeoResponse

    /** Endpoint #2: forecast by coordinates obtained from Endpoint #1 */
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "shortwave_radiation_sum,wind_speed_10m_max",
        @Query("wind_speed_unit") windUnit: String = "ms",
        @Query("forecast_days") days: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponse
}