package com.xaarlox.task12_microgrid_error_handling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task12_microgrid_error_handling.ui.AppRoot
import com.xaarlox.task12_microgrid_error_handling.ui.AppViewModelFactory
import com.xaarlox.task12_microgrid_error_handling.ui.log.LogViewModel
import com.xaarlox.task12_microgrid_error_handling.ui.stations.StationsViewModel
import com.xaarlox.task12_microgrid_error_handling.ui.theme.EcoGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                val container = (application as MicrogridApp).container
                val factory = remember { AppViewModelFactory(container) }
                val stationsViewModel: StationsViewModel = viewModel(factory = factory)
                val logViewModel: LogViewModel = viewModel(factory = factory)
                AppRoot(stationsViewModel, logViewModel)
            }
        }
    }
}