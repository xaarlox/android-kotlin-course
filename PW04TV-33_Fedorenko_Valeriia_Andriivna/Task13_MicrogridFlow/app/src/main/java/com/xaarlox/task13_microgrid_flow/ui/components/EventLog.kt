package com.xaarlox.task13_microgrid_flow.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task13_microgrid_flow.domain.EventLevel
import com.xaarlox.task13_microgrid_flow.domain.StreamEvent
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOk
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOkDark
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarn
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarnDark

/** Random-fault toggle and the log of automatic stream recovery events */
@Composable
fun FaultsAndLogCard(
    events: List<StreamEvent>,
    randomFaultsEnabled: Boolean,
    onRandomFaultsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Випадкові збої датчиків",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Потоки відновлюються автоматично (retryWhen)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = randomFaultsEnabled, onCheckedChange = onRandomFaultsChange)
            }

            Text(
                "Журнал подій",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (events.isEmpty()) {
                Text(
                    "Подій поки немає. Натисніть «Збій» біля будь-якого датчика.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            events.forEach { event ->
                val color = when (event.level) {
                    EventLevel.INFO -> MaterialTheme.colorScheme.onSurface
                    EventLevel.WARNING -> if (dark) StatusWarnDark else StatusWarn
                    EventLevel.SUCCESS -> if (dark) StatusOkDark else StatusOk
                }
                Text(
                    "${formatTime(event.timeMs)}  ${event.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
    }
}