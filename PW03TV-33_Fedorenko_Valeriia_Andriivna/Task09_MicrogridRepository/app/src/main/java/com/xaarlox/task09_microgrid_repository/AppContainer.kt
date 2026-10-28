package com.xaarlox.task09_microgrid_repository

import android.content.Context
import com.xaarlox.task09_microgrid_repository.data.MicrogridRepository
import com.xaarlox.task09_microgrid_repository.data.local.MicrogridDatabase
import com.xaarlox.task09_microgrid_repository.data.remote.ForecastRemoteDataSource

/**
 * Manual DI container (without Hilt): a single place where dependencies are assembled
 * lazy: objects are created on first access and live as long as the container lives
 */
class AppContainer(context: Context) {
    private val database by lazy { MicrogridDatabase.get(context.applicationContext) }
    private val remote by lazy { ForecastRemoteDataSource() }

    val repository: MicrogridRepository by lazy {
        MicrogridRepository(dao = database.powerDao(), remote = remote)
    }
}