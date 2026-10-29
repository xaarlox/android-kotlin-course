package com.xaarlox.task10_microgrid_rest_api.domain

import java.time.LocalDate

/** Domain model: daily microgrid generation forecast */
data class DayForecast(
    val date: LocalDate,
    val solarKwh: Double,
    val windKwh: Double
) {
    val totalKwh: Double get() = solarKwh + windKwh
}