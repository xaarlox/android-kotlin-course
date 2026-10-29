package com.xaarlox.task10_microgrid_rest_api.ui.forecast

import com.xaarlox.task10_microgrid_rest_api.domain.DayForecast
import com.xaarlox.task10_microgrid_rest_api.domain.ForecastSummary

data class ForecastUiState(
    val days: List<DayForecast> = emptyList(),
    val summary: ForecastSummary = ForecastSummary(),
    val location: String? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)