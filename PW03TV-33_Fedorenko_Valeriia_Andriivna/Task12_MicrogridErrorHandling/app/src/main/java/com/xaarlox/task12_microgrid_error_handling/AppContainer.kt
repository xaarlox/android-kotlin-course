package com.xaarlox.task12_microgrid_error_handling

import android.content.Context
import com.xaarlox.task12_microgrid_error_handling.data.StationsRepository
import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogRepository
import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogger
import com.xaarlox.task12_microgrid_error_handling.data.network.ConnectivityChecker
import com.xaarlox.task12_microgrid_error_handling.data.network.NetworkExecutor
import com.xaarlox.task12_microgrid_error_handling.data.remote.RetrofitProvider
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultSimulator

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val simulator = FaultSimulator()
    val connectivity = ConnectivityChecker(appContext, simulator)
    private val errorLogger = ErrorLogger(appContext)
    private val executor = NetworkExecutor(connectivity, errorLogger)
    private val api = RetrofitProvider.create(simulator)

    val stationsRepository = StationsRepository(api, executor)
    val errorLogRepository = ErrorLogRepository(errorLogger, api, executor)
}