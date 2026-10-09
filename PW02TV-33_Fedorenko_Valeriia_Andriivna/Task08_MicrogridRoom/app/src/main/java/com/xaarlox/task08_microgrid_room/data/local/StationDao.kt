package com.xaarlox.task08_microgrid_room.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the local data source
 * This is an abstract class (rather than an interface) because the transactional method contains a concrete implementation
 */
@Dao
abstract class StationDao {

    /**
     * Reactive query: Room automatically emits a new list whenever the
     * stations or readings tables change, keeping the UI updated without manual refreshes
     */
    @Transaction
    @Query("SELECT * FROM stations")
    abstract fun observeStations(): Flow<List<StationWithReadings>>

    /** All station names (used for case-insensitive uniqueness checks) */
    @Query("SELECT name FROM stations")
    abstract suspend fun getAllNames(): List<String>

    @Insert
    abstract suspend fun insertStation(station: StationEntity): Long

    @Insert
    abstract suspend fun insertReadings(readings: List<ReadingEntity>)

    @Query("DELETE FROM stations WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    /** Atomically persists a station along with its measurements: all-or-nothing */
    @Transaction
    open suspend fun insertWithReadings(
        station: StationEntity, powers: List<Double>, voltages: List<Double>
    ): Long {
        val stationId = insertStation(station)
        insertReadings(powers.map {
            ReadingEntity(
                stationId = stationId, kind = ReadingKind.POWER, value = it
            )
        } + voltages.map {
            ReadingEntity(
                stationId = stationId, kind = ReadingKind.VOLTAGE, value = it
            )
        })
        return stationId
    }
}