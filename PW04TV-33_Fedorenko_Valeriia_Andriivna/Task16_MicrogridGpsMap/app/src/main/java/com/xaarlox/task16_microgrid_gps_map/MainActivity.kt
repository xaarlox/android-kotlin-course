package com.xaarlox.task16_microgrid_gps_map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task16_microgrid_gps_map.ui.GpsMapScreen
import com.xaarlox.task16_microgrid_gps_map.ui.GpsMapViewModel
import com.xaarlox.task16_microgrid_gps_map.ui.theme.EcoGridTheme
import com.xaarlox.task16_microgrid_gps_map.ui.theme.advancedScreenBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                // AndroidViewModel is created by the default factory (it only needs the Application)
                val viewModel: GpsMapViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = advancedScreenBackground()
                ) {
                    GpsMapScreen(viewModel)
                }
            }
        }
    }
}