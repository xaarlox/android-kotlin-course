package com.xaarlox.task15_microgrid_filters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task15_microgrid_filters.ui.ObjectsScreen
import com.xaarlox.task15_microgrid_filters.ui.ObjectsViewModel
import com.xaarlox.task15_microgrid_filters.ui.theme.EcoGridTheme
import com.xaarlox.task15_microgrid_filters.ui.theme.advancedScreenBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                // The ViewModel survives screen rotation: filters, sorting and selection are not lost
                val viewModel: ObjectsViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = advancedScreenBackground()
                ) {
                    ObjectsScreen(viewModel)
                }
            }
        }
    }
}