package com.xaarlox.task10_microgrid_rest_api.domain

/** Aggregated metrics for the entire forecast period */
data class ForecastSummary(
    val totalKwh: Double = 0.0,
    val averageKwh: Double = 0.0,
    val bestDay: DayForecast? = null,
    val solarShare: Double = 0.0   // solar share in generation, 0..1
) {
    companion object {
        fun from(days: List<DayForecast>): ForecastSummary {
            if (days.isEmpty()) return ForecastSummary()
            val total = days.sumOf { it.totalKwh }
            return ForecastSummary(
                totalKwh = total,
                averageKwh = total / days.size,
                bestDay = days.maxBy { it.totalKwh },
                solarShare = if (total > 0) days.sumOf { it.solarKwh } / total else 0.0
            )
        }
    }
}