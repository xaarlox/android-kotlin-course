package com.xaarlox.task13_microgrid_flow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task13_microgrid_flow.domain.ForecastAccuracy

/** Accuracy of the solar forecast — the result of the zip operator (actual ↔ forecast, pairwise) */
@Composable
fun ForecastCard(accuracy: ForecastAccuracy?, modifier: Modifier = Modifier) {
    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "Точність прогнозу сонця",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Результат попарного об'єднання (zip)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (accuracy == null) {
                Text(
                    "Очікування пар «факт — прогноз»…",
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Факт / прогноз", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${formatKw(accuracy.actualKw)} / ${formatKw(accuracy.forecastKw)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Відхилення", style = MaterialTheme.typography.bodyMedium)
                Text(
                    accuracy.errorPercent?.let {
                        "${
                            formatSigned(
                                accuracy.errorKw,
                                "кВт"
                            )
                        } (${formatSigned(it, "%")})"
                    }
                        ?: "ніч — прогноз ≈ 0",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}