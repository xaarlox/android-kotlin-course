package com.xaarlox.task10_microgrid_rest_api.ui.forecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task10_microgrid_rest_api.ui.components.CitySearchBar
import com.xaarlox.task10_microgrid_rest_api.ui.components.DayForecastCard
import com.xaarlox.task10_microgrid_rest_api.ui.components.SummaryCard

@Composable
fun ForecastScreen(viewModel: ForecastViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
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
                            state.location ?: "Прогноз виробітку на 7 днів",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    SummaryCard(state.summary)
                    CitySearchBar(
                        query = state.query,
                        isLoading = state.isLoading,
                        onQueryChange = viewModel::onQueryChange,
                        onSearch = viewModel::search
                    )
                }
            }
            if (state.days.isEmpty()) {
                item {
                    Text(
                        if (state.isLoading) "Завантаження…" else "Даних немає",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                val best = state.summary.bestDay?.date
                val maxKwh = state.summary.bestDay?.totalKwh ?: 1.0
                items(state.days, key = { it.date.toString() }) { day ->
                    DayForecastCard(day, isBest = day.date == best, maxKwh = maxKwh)
                }
            }
        }
    }
}