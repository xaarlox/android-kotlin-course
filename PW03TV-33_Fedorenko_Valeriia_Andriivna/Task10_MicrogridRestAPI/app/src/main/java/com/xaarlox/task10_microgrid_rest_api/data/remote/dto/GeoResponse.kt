package com.xaarlox.task10_microgrid_rest_api.data.remote.dto

/** If the city is not found, Open-Meteo omits the "results" field entirely: therefore, it is nullable */
data class GeoResponse(val results: List<GeoResultDto>?)

data class GeoResultDto(
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)