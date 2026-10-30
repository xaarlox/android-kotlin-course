package com.xaarlox.task12_microgrid_error_handling.ui.stations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.ui.components.BannerKind
import com.xaarlox.task12_microgrid_error_handling.ui.components.ErrorState
import com.xaarlox.task12_microgrid_error_handling.ui.components.RetryProgress
import com.xaarlox.task12_microgrid_error_handling.ui.components.ScreenHeader
import com.xaarlox.task12_microgrid_error_handling.ui.components.StationCard
import com.xaarlox.task12_microgrid_error_handling.ui.components.StatusBanner
import com.xaarlox.task12_microgrid_error_handling.ui.formatTime

@Composable
fun StationsScreen(
    state: StationsUiState,
    onRefresh: () -> Unit,
    onToggle: (Int) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ScreenHeader(
                title = "Станції",
                subtitle = state.lastUpdatedMs?.let { "Оновлено о ${formatTime(it)}" }
                    ?: "Дані ще не завантажено"
            ) {
                IconButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Оновити")
                }
            }
        }

        if (!state.isOnline) {
            item {
                StatusBanner(
                    text = if (state.stations.isEmpty()) "Немає з'єднання з мережею"
                    else "Немає з'єднання. Показано збережені дані",
                    kind = BannerKind.WARNING,
                    icon = Icons.Default.WifiOff
                )
            }
        }

        state.retry?.let { info -> item { RetryProgress(info) } }

        state.failure?.let { failure ->
            item {
                if (state.stations.isEmpty()) {
                    ErrorState(
                        error = failure.error,
                        attempts = failure.attempts,
                        isLoading = state.isLoading,
                        onRetry = onRefresh
                    )
                } else {
                    StatusBanner(
                        text = "${failure.error.title}. Показано збережені дані",
                        kind = BannerKind.ERROR,
                        icon = Icons.Default.ErrorOutline
                    )
                }
            }
        }

        if (state.stations.isEmpty() && state.failure == null) {
            item {
                Text(
                    if (state.isLoading) "Завантаження…" else "Станцій немає",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )
            }
        }

        items(state.stations, key = { it.id }) { station ->
            StationCard(
                station = station,
                busy = state.busyStationId != null,
                onToggle = { onToggle(station.id) }
            )
        }
    }
}