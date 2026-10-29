package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.ScreenHeader
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.StationCard

@Composable
fun StationsScreen(
    state: PortalUiState,
    isAdmin: Boolean,
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
                subtitle = "Онлайн: ${state.stations.count { it.online }} з ${state.stations.size}"
            ) {
                IconButton(onClick = onRefresh, enabled = !state.isLoading) {
                    Icon(Icons.Default.Refresh, contentDescription = "Оновити")
                }
            }
        }
        if (state.stations.isEmpty()) {
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
        } else {
            items(state.stations, key = { it.id }) { station ->
                StationCard(
                    station = station,
                    canManage = isAdmin,
                    busy = state.busyStationId != null,
                    onToggle = { onToggle(station.id) }
                )
            }
        }
    }
}