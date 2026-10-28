package com.xaarlox.task09_microgrid_repository

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task09_microgrid_repository.ui.advanced.AdvancedScreen
import com.xaarlox.task09_microgrid_repository.ui.advanced.AdvancedViewModel
import com.xaarlox.task09_microgrid_repository.ui.advanced.AdvancedViewModelFactory
import com.xaarlox.task09_microgrid_repository.ui.theme.EcoGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                val appContext = LocalContext.current.applicationContext
                // The factory is called only when the ViewModel is created for the first time;
                // after that, the ViewModel survives screen rotations
                val factory =
                    remember { AdvancedViewModelFactory(AppContainer(appContext).repository) }
                val viewModel: AdvancedViewModel = viewModel(factory = factory)

                AdvancedScreen(viewModel)
            }
        }
    }
}