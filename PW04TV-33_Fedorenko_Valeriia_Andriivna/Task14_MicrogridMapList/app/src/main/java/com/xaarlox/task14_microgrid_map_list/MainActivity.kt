package com.xaarlox.task14_microgrid_map_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task14_microgrid_map_list.ui.MapListScreen
import com.xaarlox.task14_microgrid_map_list.ui.MapListViewModel
import com.xaarlox.task14_microgrid_map_list.ui.theme.EcoGridTheme
import com.xaarlox.task14_microgrid_map_list.ui.theme.advancedScreenBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                // The ViewModel survives screen rotation, so the selection is not lost
                val viewModel: MapListViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = advancedScreenBackground()
                ) {
                    MapListScreen(viewModel)
                }
            }
        }
    }
}