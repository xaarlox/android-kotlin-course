package com.xaarlox.task13_microgrid_flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task13_microgrid_flow.ui.MicrogridScreen
import com.xaarlox.task13_microgrid_flow.ui.MicrogridViewModel
import com.xaarlox.task13_microgrid_flow.ui.theme.EcoGridTheme
import com.xaarlox.task13_microgrid_flow.ui.theme.advancedScreenBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                // The ViewModel survives screen rotation, so the hot flows are not restarted
                val viewModel: MicrogridViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = advancedScreenBackground()
                ) {
                    MicrogridScreen(viewModel)
                }
            }
        }
    }
}