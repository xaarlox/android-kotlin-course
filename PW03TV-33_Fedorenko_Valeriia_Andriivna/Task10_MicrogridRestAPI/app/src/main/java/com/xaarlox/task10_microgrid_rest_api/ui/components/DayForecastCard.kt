package com.xaarlox.task10_microgrid_rest_api.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task10_microgrid_rest_api.domain.DayForecast
import com.xaarlox.task10_microgrid_rest_api.ui.formatKwh
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("uk"))

@Composable
fun DayForecastCard(
    day: DayForecast,
    isBest: Boolean,
    maxKwh: Double,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    AppCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isBest) scheme.primaryContainer.copy(alpha = 0.35f) else scheme.surface
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    day.date.format(dayFormat).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Text(formatKwh(day.totalKwh), style = MaterialTheme.typography.titleMedium)
            }
            LinearProgressIndicator(
                progress = { (day.totalKwh / maxKwh).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = scheme.primary,
                trackColor = scheme.outline.copy(alpha = 0.2f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Сонце: ${formatKwh(day.solarKwh)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    "Вітер: ${formatKwh(day.windKwh)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}