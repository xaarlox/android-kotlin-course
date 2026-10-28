package com.xaarlox.task09_microgrid_repository.ui.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task09_microgrid_repository.ui.components.AddRecordForm
import com.xaarlox.task09_microgrid_repository.ui.components.FilterSortBar
import com.xaarlox.task09_microgrid_repository.ui.components.StatsCard

/** Top section of the screen: header, statistics, add entry form, forecasts, and filter controls */
@Composable
fun ControlsPanel(state: UiState, viewModel: AdvancedViewModel) {
    Column(
        Modifier.padding(top = 24.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                "Мікрогрід",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Виробіток енергії за днями",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        StatsCard(state.stats)

        AddRecordForm(onAdd = viewModel::addRecord)

        OutlinedButton(
            onClick = viewModel::loadForecasts,
            enabled = !state.isLoading,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Завантажити прогнози")
            }
        }

        FilterSortBar(
            filter = state.dayFilter,
            sortAscending = state.sortAscending,
            onFilterChange = viewModel::onDayFilterChange,
            onToggleSort = viewModel::toggleSort
        )
    }
}