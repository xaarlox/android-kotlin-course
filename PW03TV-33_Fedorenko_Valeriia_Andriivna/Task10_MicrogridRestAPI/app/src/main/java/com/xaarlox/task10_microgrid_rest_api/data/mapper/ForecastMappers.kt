package com.xaarlox.task10_microgrid_rest_api.data.mapper

import com.xaarlox.task10_microgrid_rest_api.data.local.ForecastEntity
import com.xaarlox.task10_microgrid_rest_api.data.remote.InvalidResponseException
import com.xaarlox.task10_microgrid_rest_api.data.remote.dto.ForecastResponse
import com.xaarlox.task10_microgrid_rest_api.domain.DayForecast
import com.xaarlox.task10_microgrid_rest_api.domain.EnergyEstimator
import com.xaarlox.task10_microgrid_rest_api.domain.StationConfig
import java.time.LocalDate

/** Validation + combining parallel arrays into a list of entities. Days with null values are skipped */
fun ForecastResponse.toEntities(location: String): List<ForecastEntity> {
    val daily = daily ?: throw InvalidResponseException()
    val dates = daily.time ?: throw InvalidResponseException()
    val radiation = daily.radiationSumMj ?: throw InvalidResponseException()
    val wind = daily.windMaxMs ?: throw InvalidResponseException()
    if (dates.size != radiation.size || dates.size != wind.size) throw InvalidResponseException()

    return dates.indices.mapNotNull { i ->
        val r = radiation[i] ?: return@mapNotNull null
        val w = wind[i] ?: return@mapNotNull null
        ForecastEntity(date = dates[i], radiationMj = r, windMaxMs = w, location = location)
    }.also { if (it.isEmpty()) throw InvalidResponseException() }
}

fun ForecastEntity.toDomain(config: StationConfig) = DayForecast(
    date = LocalDate.parse(date),
    solarKwh = EnergyEstimator.solarKwh(radiationMj, config),
    windKwh = EnergyEstimator.windKwh(windMaxMs, config)
)