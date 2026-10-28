package com.xaarlox.task09_microgrid_repository.data.mapper

import com.xaarlox.task09_microgrid_repository.data.local.PowerEntity
import com.xaarlox.task09_microgrid_repository.data.remote.ForecastDto
import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import com.xaarlox.task09_microgrid_repository.domain.RecordSource

fun PowerEntity.toDomain() = PowerRecord(
    id = id,
    day = day,
    powerKw = powerKw,
    source = RecordSource.valueOf(source)
)

fun ForecastDto.toEntity() = PowerEntity(
    day = day,
    powerKw = powerKw,
    source = RecordSource.FORECAST.name
)