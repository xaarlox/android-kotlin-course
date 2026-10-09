package com.xaarlox.task08_microgrid_room

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.xaarlox.task08_microgrid_room.ui.screens.StationScreen
import com.xaarlox.task08_microgrid_room.ui.theme.EcoGridTheme
import com.xaarlox.task08_microgrid_room.viewmodel.StationViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StationViewModel by viewModels {
        StationViewModel.factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StationScreen(viewModel)
                }
            }
        }
    }
}