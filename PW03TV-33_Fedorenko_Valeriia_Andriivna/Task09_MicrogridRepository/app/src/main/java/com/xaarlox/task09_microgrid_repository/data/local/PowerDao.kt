package com.xaarlox.task09_microgrid_repository.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO — the single point of access to SQL
 *
 * This is an abstract class rather than an interface so that a @Transaction method
 * with a body (replaceBySource) can be written without configuring jvm-default
 *
 * observeAll() returns a Flow: Room automatically re-emits the list after every table change,
 * allowing the UI to update "reactively" without manual "refresh list" calls
 */
@Dao
abstract class PowerDao {

    @Query("SELECT * FROM power_records")
    abstract fun observeAll(): Flow<List<PowerEntity>>

    @Insert
    abstract suspend fun insert(entity: PowerEntity): Long

    @Insert
    abstract suspend fun insertAll(entities: List<PowerEntity>)

    @Query("DELETE FROM power_records WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM power_records WHERE source = :source")
    abstract suspend fun deleteBySource(source: String)

    /**
     * Atomically replaces all records from a single origin
     *
     * The transaction guarantees that the UI will never see the intermediate state where "old items are deleted, but new ones are not yet added"
     */
    @Transaction
    open suspend fun replaceBySource(source: String, entities: List<PowerEntity>) {
        deleteBySource(source)
        insertAll(entities)
    }
}