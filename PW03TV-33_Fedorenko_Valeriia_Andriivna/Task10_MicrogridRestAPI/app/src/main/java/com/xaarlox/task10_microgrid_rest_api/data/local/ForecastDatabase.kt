package com.xaarlox.task10_microgrid_rest_api.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ForecastEntity::class], version = 1, exportSchema = false)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        private var instance: ForecastDatabase? = null

        fun get(context: Context): ForecastDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ForecastDatabase::class.java,
                    "microgrid_forecast.db"
                ).build().also { instance = it }
            }
    }
}