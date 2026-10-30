package com.xaarlox.task12_microgrid_error_handling.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xaarlox.task12_microgrid_error_handling.AppContainer
import com.xaarlox.task12_microgrid_error_handling.ui.log.LogViewModel
import com.xaarlox.task12_microgrid_error_handling.ui.stations.StationsViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(StationsViewModel::class.java) ->
            StationsViewModel(
                container.stationsRepository,
                container.connectivity,
                container.simulator
            )

        modelClass.isAssignableFrom(LogViewModel::class.java) ->
            LogViewModel(container.errorLogRepository)

        else -> error("Unknown ViewModel: ${modelClass.name}")
    } as T
}