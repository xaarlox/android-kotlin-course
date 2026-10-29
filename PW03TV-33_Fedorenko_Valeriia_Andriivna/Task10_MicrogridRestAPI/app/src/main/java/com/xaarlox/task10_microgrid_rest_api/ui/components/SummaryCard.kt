package com.xaarlox.task10_microgrid_rest_api.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.xaarlox.task10_microgrid_rest_api.domain.ForecastSummary
import com.xaarlox.task10_microgrid_rest_api.ui.formatKwh
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortDate = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("uk"))

@Composable
fun SummaryCard(summary: ForecastSummary, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column {
                Text(
                    "Прогноз виробітку за тиждень",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.75f)
                )
                Text(formatKwh(summary.totalKwh), style = MaterialTheme.typography.displaySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Metric("Середнє за день", formatKwh(summary.averageKwh))
                Metric("Найкращий день", summary.bestDay?.date?.format(shortDate) ?: "—")
                Metric("Частка сонця", "${(summary.solarShare * 100).toInt()}%")
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