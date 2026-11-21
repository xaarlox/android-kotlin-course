package com.xaarlox.task13_microgrid_flow.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import com.xaarlox.task13_microgrid_flow.domain.SourceStatus
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOk
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOkDark
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarn
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarnDark

/** Card of one source: current value, connection status and a fault-injection button */
@Composable
fun SourceCard(
    kind: SensorKind,
    valueText: String,
    status: SourceStatus,
    onInjectFault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    val ok = if (dark) StatusOkDark else StatusOk
    val warn = if (dark) StatusWarnDark else StatusWarn

    val (statusText, statusColor) = when (status) {
        SourceStatus.Connecting -> "Підключення…" to MaterialTheme.colorScheme.onSurfaceVariant
        SourceStatus.Online -> "Онлайн" to ok
        is SourceStatus.Reconnecting -> "Перепідключення, спроба №${status.attempt}" to warn
    }

    AppCard(modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    kind.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(valueText, style = MaterialTheme.typography.titleMedium)
                StatusLabel(statusText, statusColor)
            }
            OutlinedButton(onClick = onInjectFault) { Text("Збій") }
        }
    }
}

@Composable
private fun StatusLabel(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Medium
    )
}