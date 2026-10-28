package com.xaarlox.task09_microgrid_repository.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data-layer model (a row in the Room table)
 *
 * Architectural decision: Entity lives strictly within the data layer and does not "leak" into the UI
 *
 * The Repository exposes the PowerRecord domain model externally (see domain/PowerRecord.kt),
 * ensuring that database schema changes do not affect the ViewModel or Compose screens
 *
 * The [source] field distinguishes the record's origin: entered manually (MANUAL)
 * or downloaded from a remote forecast source (FORECAST)
 */
@Entity(tableName = "power_records")
data class PowerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: Int,
    val powerKw: Double,
    val source: String
)