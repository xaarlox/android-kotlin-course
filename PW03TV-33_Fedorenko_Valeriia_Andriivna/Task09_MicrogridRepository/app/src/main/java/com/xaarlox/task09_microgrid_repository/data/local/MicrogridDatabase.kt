package com.xaarlox.task09_microgrid_repository.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PowerEntity::class], version = 1, exportSchema = false)
abstract class MicrogridDatabase : RoomDatabase() {

    abstract fun powerDao(): PowerDao

    companion object {
        @Volatile
        private var instance: MicrogridDatabase? = null

        /** Singleton: multiple Room database instances in a single process are a common cause of bugs and excessive memory usage */
        fun get(context: Context): MicrogridDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MicrogridDatabase::class.java,
                    "microgrid.db"
                ).build().also { instance = it }
            }
    }
}