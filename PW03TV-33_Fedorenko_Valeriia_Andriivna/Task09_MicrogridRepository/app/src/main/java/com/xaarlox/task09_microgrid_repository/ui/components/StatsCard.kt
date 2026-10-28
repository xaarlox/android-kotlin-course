package com.xaarlox.task09_microgrid_repository.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.xaarlox.task09_microgrid_repository.domain.PowerStats
import com.xaarlox.task09_microgrid_repository.ui.formatKw

/** The single accent block on the screen: the primary metric with three supporting indicators */
@Composable
fun StatsCard(stats: PowerStats, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Text(
                    "Сумарна потужність",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.75f)
                )
                Text(formatKw(stats.totalKw), style = MaterialTheme.typography.displaySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Metric("Середнє", formatKw(stats.averageKw))
                Metric(
                    "Пік",
                    stats.peakDay?.let { "день $it" } ?: "—"
                )
                Metric("Записів", stats.count.toString())
            }
        }
    }
}

@Composable
private fun RowScope.Metric(label: String, value: String) {
    Column(Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.alpha(0.75f))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}