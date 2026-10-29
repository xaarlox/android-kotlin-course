package com.xaarlox.task10_microgrid_rest_api

import android.content.Context
import com.xaarlox.task10_microgrid_rest_api.data.ForecastRepository
import com.xaarlox.task10_microgrid_rest_api.data.local.ForecastDatabase
import com.xaarlox.task10_microgrid_rest_api.data.remote.RetrofitProvider

class AppContainer(context: Context) {
    private val database by lazy { ForecastDatabase.get(context.applicationContext) }

    val repository: ForecastRepository by lazy {
        ForecastRepository(dao = database.forecastDao(), api = RetrofitProvider.openMeteoApi)
    }
}