package com.xaarlox.task10_microgrid_rest_api

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task10_microgrid_rest_api.ui.forecast.ForecastScreen
import com.xaarlox.task10_microgrid_rest_api.ui.forecast.ForecastViewModel
import com.xaarlox.task10_microgrid_rest_api.ui.forecast.ForecastViewModelFactory
import com.xaarlox.task10_microgrid_rest_api.ui.theme.EcoGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                val appContext = LocalContext.current.applicationContext
                val factory =
                    remember { ForecastViewModelFactory(AppContainer(appContext).repository) }
                val viewModel: ForecastViewModel = viewModel(factory = factory)
                ForecastScreen(viewModel)
            }
        }
    }
}