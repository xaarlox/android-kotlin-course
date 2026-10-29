package com.xaarlox.task10_microgrid_rest_api.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ForecastDao {

    @Query("SELECT * FROM forecast_days ORDER BY date")
    abstract fun observeAll(): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<ForecastEntity>)

    @Query("DELETE FROM forecast_days")
    abstract suspend fun clear()

    /** Atomic replacement: the UI will not see the intermediate state "old deleted, new not yet added" */
    @Transaction
    open suspend fun replaceAll(items: List<ForecastEntity>) {
        clear()
        insertAll(items)
    }
}