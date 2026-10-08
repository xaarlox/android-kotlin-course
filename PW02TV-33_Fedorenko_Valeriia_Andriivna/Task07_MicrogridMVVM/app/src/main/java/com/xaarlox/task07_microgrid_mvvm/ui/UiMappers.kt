package com.xaarlox.task07_microgrid_mvvm.ui

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.xaarlox.task07_microgrid_mvvm.R
import com.xaarlox.task07_microgrid_mvvm.domain.EnergySource
import com.xaarlox.task07_microgrid_mvvm.domain.PowerStatus

/** Source name for the card header */
@StringRes
fun EnergySource.titleRes(): Int = when (this) {
    EnergySource.SOLAR -> R.string.source_solar_title
    EnergySource.WIND -> R.string.source_wind_title
}

/** Power status text */
@StringRes
fun PowerStatus.labelRes(): Int = when (this) {
    PowerStatus.LOW -> R.string.status_low
    PowerStatus.NORMAL -> R.string.status_normal
    PowerStatus.HIGH -> R.string.status_high
}

/** Status text color */
@Composable
fun PowerStatus.color(): Color = when (this) {
    PowerStatus.LOW -> MaterialTheme.colorScheme.tertiary
    PowerStatus.NORMAL -> MaterialTheme.colorScheme.onSurface
    PowerStatus.HIGH -> MaterialTheme.colorScheme.primary
}