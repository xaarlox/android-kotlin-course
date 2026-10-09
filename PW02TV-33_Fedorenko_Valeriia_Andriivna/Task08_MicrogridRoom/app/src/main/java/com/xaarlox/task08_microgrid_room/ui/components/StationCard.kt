package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.Station
import com.xaarlox.task08_microgrid_room.domain.StationAnalytics
import com.xaarlox.task08_microgrid_room.ui.format1
import com.xaarlox.task08_microgrid_room.ui.formatList

/**
 * Single station card: displays the name, derived metrics (average, peak), power level,
 * and lists of measurements. Reused for each item in the station list
 *
 * @param station Station entity to display
 * @param onDelete Callback invoked to delete the station
 * @param modifier External modifier to be applied to the card
 */
@Composable
fun StationCard(
    station: Station,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHigh = station.avgPower >= StationAnalytics.HIGH_POWER_THRESHOLD_W

    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_button))
            }
        }
        Text(text = stringResource(R.string.card_avg_power, station.avgPower.format1()))
        Text(text = stringResource(R.string.card_max_power, station.maxPower.format1()))
        Text(text = stringResource(R.string.card_avg_voltage, station.avgVoltage.format1()))
        Text(
            text = stringResource(if (isHigh) R.string.level_high else R.string.level_low),
            color = if (isHigh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        )
        Text(text = stringResource(R.string.card_powers, station.powers.formatList()))
        Text(text = stringResource(R.string.card_voltages, station.voltages.formatList()))
    }
}