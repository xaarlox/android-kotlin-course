package com.xaarlox.task09_microgrid_repository.domain

/**
 * Domain model: this is what the ViewModel and UI directly interact with
 * It has no knowledge of Room or the network (layer isolation)
 */
data class PowerRecord(
    val id: Long,
    val day: Int,
    val powerKw: Double,
    val source: RecordSource
)