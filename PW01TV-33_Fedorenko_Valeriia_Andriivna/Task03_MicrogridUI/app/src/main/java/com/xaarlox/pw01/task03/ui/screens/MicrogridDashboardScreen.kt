package com.xaarlox.pw01.task03.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.pw01.task03.logic.calculateTotalOutput
import com.xaarlox.pw01.task03.logic.isBelowThreshold
import com.xaarlox.pw01.task03.logic.toggleSourceState
import com.xaarlox.pw01.task03.model.EnergySource
import com.xaarlox.pw01.task03.ui.components.DashboardHeader
import com.xaarlox.pw01.task03.ui.components.SourceCard

// Starting set of energy sources shown when the screen first loads
private fun initialSources() = listOf(
    EnergySource(
        id = "solar",
        name = "Сонячна панель",
        icon = Icons.Default.WbSunny,
        outputWatts = 650,
        isActive = true
    ),
    EnergySource(
        id = "wind",
        name = "Вітрогенератор",
        icon = Icons.Default.WaterDrop,
        outputWatts = 320,
        isActive = false
    ),
    EnergySource(
        id = "battery",
        name = "Акумулятор (BESS)",
        icon = Icons.Default.BatteryChargingFull,
        outputWatts = 200,
        isActive = true
    )
)

@Composable
fun MicrogridDashboardScreen() {
    // Single source of truth: the whole grid re-renders whenever this list changes
    var sources by remember { mutableStateOf(initialSources()) }
    var thresholdText by remember { mutableStateOf("500") }

    // Recalculated on every recomposition, so it always reflects the current sources
    val totalOutput = calculateTotalOutput(sources)
    val belowThreshold = isBelowThreshold(totalOutput, thresholdText)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DashboardHeader(totalOutputWatts = totalOutput)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = thresholdText,
            onValueChange = { thresholdText = it },
            label = { Text("Поріг попередження (Вт)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (belowThreshold == true) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Генерація нижча за встановлений поріг!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        // Adaptive: column count adjusts automatically to the available screen width
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // key = source.id helps Compose track items correctly across recompositions
            items(sources, key = { it.id }) { source ->
                SourceCard(
                    source = source,
                    onToggle = {
                        sources = toggleSourceState(sources, source.id)
                    }
                )
            }
        }
    }
}