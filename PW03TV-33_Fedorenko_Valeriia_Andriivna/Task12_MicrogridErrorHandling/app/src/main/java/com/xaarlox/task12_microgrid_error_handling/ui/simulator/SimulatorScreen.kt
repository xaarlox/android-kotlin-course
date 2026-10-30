package com.xaarlox.task12_microgrid_error_handling.ui.simulator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultScenario
import com.xaarlox.task12_microgrid_error_handling.ui.components.ScenarioRow
import com.xaarlox.task12_microgrid_error_handling.ui.components.ScreenHeader

@Composable
fun SimulatorScreen(selected: FaultScenario, onSelect: (FaultScenario) -> Unit) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenHeader("Симулятор збоїв", "Оберіть сценарій, потім оновіть «Станції»") }
        items(FaultScenario.entries) { scenario ->
            ScenarioRow(scenario, selected = scenario == selected, onClick = { onSelect(scenario) })
        }
    }
}