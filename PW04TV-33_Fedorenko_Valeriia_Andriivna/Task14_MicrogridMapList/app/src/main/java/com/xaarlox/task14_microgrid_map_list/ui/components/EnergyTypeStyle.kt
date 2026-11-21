package com.xaarlox.task14_microgrid_map_list.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.xaarlox.task14_microgrid_map_list.domain.EnergyType
import com.xaarlox.task14_microgrid_map_list.ui.theme.GoldenYellow
import com.xaarlox.task14_microgrid_map_list.ui.theme.GoldenYellowDark
import com.xaarlox.task14_microgrid_map_list.ui.theme.StatusLoad
import com.xaarlox.task14_microgrid_map_list.ui.theme.StatusLoadDark
import com.xaarlox.task14_microgrid_map_list.ui.theme.StatusOk
import com.xaarlox.task14_microgrid_map_list.ui.theme.StatusOkDark
import com.xaarlox.task14_microgrid_map_list.ui.theme.SunOrange
import com.xaarlox.task14_microgrid_map_list.ui.theme.SunOrangeDark
import com.xaarlox.task14_microgrid_map_list.ui.theme.WindBlue
import com.xaarlox.task14_microgrid_map_list.ui.theme.WindBlueDark

/** The single "type → icon / color" mapping for the card, the marker and the legend */
fun typeIcon(type: EnergyType): ImageVector = when (type) {
    EnergyType.SOLAR -> Icons.Filled.WbSunny
    EnergyType.WIND -> Icons.Filled.Air
    EnergyType.STORAGE -> Icons.Filled.BatteryChargingFull
    EnergyType.CONSUMER -> Icons.Filled.Home
    EnergyType.SUBSTATION -> Icons.Filled.Bolt
}

fun typeColor(type: EnergyType, dark: Boolean): Color = when (type) {
    EnergyType.SOLAR -> if (dark) GoldenYellowDark else GoldenYellow
    EnergyType.WIND -> if (dark) WindBlueDark else WindBlue
    EnergyType.STORAGE -> if (dark) StatusOkDark else StatusOk
    EnergyType.CONSUMER -> if (dark) StatusLoadDark else StatusLoad
    EnergyType.SUBSTATION -> if (dark) SunOrangeDark else SunOrange
}