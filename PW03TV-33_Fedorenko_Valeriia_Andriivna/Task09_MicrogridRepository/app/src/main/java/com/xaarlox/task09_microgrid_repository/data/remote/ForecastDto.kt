package com.xaarlox.task09_microgrid_repository.data.remote

/** DTO — the format in which the "server" returns the generation forecast (solar panels + wind turbine) **/
data class ForecastDto(val day: Int, val powerKw: Double)