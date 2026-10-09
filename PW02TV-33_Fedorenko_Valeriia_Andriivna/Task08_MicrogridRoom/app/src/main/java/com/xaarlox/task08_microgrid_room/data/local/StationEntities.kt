package com.xaarlox.task08_microgrid_room.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Architectural decision: station and its measurements are split into two tables with a one-to-many
 * relationship (instead of storing numeric lists as a single string). This allows managing measurements
 * independently, and deleting a station cascades to all its measurements
 */
@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long
)

enum class ReadingKind { POWER, VOLTAGE }

/** A single measurement (power in W or voltage in V) belonging to a station */
@Entity(
    tableName = "readings",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["stationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("stationId")]
)
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stationId: Long,
    val kind: ReadingKind,
    val value: Double
)

/** A station along with all of its measurements (result of a @Relation query) */
data class StationWithReadings(
    @Embedded val station: StationEntity,
    @Relation(parentColumn = "id", entityColumn = "stationId")
    val readings: List<ReadingEntity>
)