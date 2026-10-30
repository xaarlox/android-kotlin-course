package com.xaarlox.task12_microgrid_error_handling.data.mapper

import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.StationDto
import com.xaarlox.task12_microgrid_error_handling.domain.Station
import com.xaarlox.task12_microgrid_error_handling.domain.StationType

fun StationDto.toDomain(): Station? {
    val stationId = id ?: return null
    val stationName = name ?: return null
    val stationType = StationType.fromCode(type) ?: return null
    return Station(stationId, stationName, stationType, powerKw ?: 0.0, online ?: false)
}