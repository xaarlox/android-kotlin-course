package com.xaarlox.task10_microgrid_rest_api.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Gson creates these objects via reflection and IGNORES Kotlin's null-safety:
 * if the server omits a field, it will become null even for non-nullable types.
 * Therefore, all untrusted fields are declared as nullable, and validation is handled in the mapper
 *
 * Open-Meteo returns "daily" data as parallel arrays: time[i] corresponds to radiation[i] and wind[i]
 */
data class ForecastResponse(val daily: DailyDto?)

data class DailyDto(
    val time: List<String>?,
    @SerializedName("shortwave_radiation_sum") val radiationSumMj: List<Double?>?,
    @SerializedName("wind_speed_10m_max") val windMaxMs: List<Double?>?
)