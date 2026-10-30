package com.xaarlox.task12_microgrid_error_handling.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.domain.Station
import com.xaarlox.task12_microgrid_error_handling.domain.StationType
import com.xaarlox.task12_microgrid_error_handling.ui.formatKw

/** Station card with an on/off toggle button. busy = true disables the button while a request is in progress */
@Composable
fun StationCard(
    station: Station,
    busy: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    AppCard(modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (station.online) scheme.primaryContainer else scheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    station.type.icon(),
                    contentDescription = null,
                    tint = if (station.online) scheme.onPrimaryContainer else scheme.onSurfaceVariant
                )
            }
            Column(Modifier.weight(1f)) {
                Text(station.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${station.type.label} · ${if (station.online) "онлайн" else "вимкнена"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    if (station.online) formatKw(station.powerKw) else "—",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedButton(
                onClick = onToggle,
                enabled = !busy,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (station.online) "Вимкнути" else "Увімкнути")
            }
        }
    }
}

private fun StationType.icon(): ImageVector = when (this) {
    StationType.SOLAR -> Icons.Default.WbSunny
    StationType.WIND -> Icons.Default.Air
    StationType.STORAGE -> Icons.Default.BatteryChargingFull
}