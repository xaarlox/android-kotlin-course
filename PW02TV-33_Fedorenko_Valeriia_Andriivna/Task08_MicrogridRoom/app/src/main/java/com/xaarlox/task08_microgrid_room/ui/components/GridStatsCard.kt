package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.GridStats
import com.xaarlox.task08_microgrid_room.ui.format1

/**
 * Microgrid summary card: displays aggregated metrics computed by the Repository
 *
 * @param stats Aggregated summary metrics
 * @param modifier External modifier to be applied to the card
 */
@Composable
fun GridStatsCard(stats: GridStats, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.titleMedium
        )
        if (stats.stationCount == 0) {
            Text(text = stringResource(R.string.stats_empty))
        } else {
            Text(text = stringResource(R.string.stats_count, stats.stationCount))
            Text(text = stringResource(R.string.stats_power, stats.totalPowerW.format1()))
            Text(text = stringResource(R.string.stats_voltage, stats.avgVoltageV.format1()))
            stats.peakStationName?.let {
                Text(text = stringResource(R.string.stats_peak, it))
            }
        }
    }
}