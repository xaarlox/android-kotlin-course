package com.xaarlox.task13_microgrid_flow.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task13_microgrid_flow.domain.BalanceMode
import com.xaarlox.task13_microgrid_flow.domain.BatteryParams
import com.xaarlox.task13_microgrid_flow.domain.MicrogridSnapshot
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOk
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusOkDark
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarn
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusWarnDark

/** Summary of the combine result: generation, load, balance, battery and grid exchange */
@Composable
fun BalanceCard(snapshot: MicrogridSnapshot?, modifier: Modifier = Modifier) {
    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Баланс мікрогріду",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Результат об'єднання потоків (combine)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (snapshot == null) {
                Text(
                    "Очікування даних від усіх датчиків…",
                    style = MaterialTheme.typography.bodyLarge
                )
                return@Column
            }

            val dark = isSystemInDarkTheme()
            val modeColor = when (snapshot.mode) {
                BalanceMode.SURPLUS -> if (dark) StatusOkDark else StatusOk
                BalanceMode.DEFICIT -> if (dark) StatusWarnDark else StatusWarn
                BalanceMode.BALANCED -> MaterialTheme.colorScheme.onSurface
            }

            ValueRow("Генерація (сонце + вітер)", formatKw(snapshot.generationKw))
            ValueRow("Навантаження", formatKw(snapshot.loadKw))
            ValueRow(
                "Баланс",
                formatSigned(snapshot.balanceKw, "кВт"),
                valueColor = modeColor,
                bold = true
            )
            Text(
                snapshot.mode.title,
                style = MaterialTheme.typography.bodyMedium,
                color = modeColor
            )

            Spacer(Modifier.height(4.dp))
            ValueRow("Заряд батареї", formatPercent(snapshot.batterySocPercent))
            LinearProgressIndicator(
                progress = {
                    (snapshot.batterySocPercent / BatteryParams.MAX_SOC).toFloat().coerceIn(0f, 1f)
                },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            ValueRow(
                "Батарея",
                when {
                    snapshot.batteryPowerKw > 0.05 -> "заряджається ${formatKw(snapshot.batteryPowerKw)}"
                    snapshot.batteryPowerKw < -0.05 -> "розряджається ${formatKw(-snapshot.batteryPowerKw)}"
                    else -> "простоює"
                }
            )
            ValueRow(
                "Зовнішня мережа",
                when {
                    snapshot.gridKw > 0.05 -> "експорт ${formatKw(snapshot.gridKw)}"
                    snapshot.gridKw < -0.05 -> "імпорт ${formatKw(-snapshot.gridKw)}"
                    else -> "не використовується"
                }
            )
        }
    }
}

@Composable
private fun ValueRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = false
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium
        )
    }
}