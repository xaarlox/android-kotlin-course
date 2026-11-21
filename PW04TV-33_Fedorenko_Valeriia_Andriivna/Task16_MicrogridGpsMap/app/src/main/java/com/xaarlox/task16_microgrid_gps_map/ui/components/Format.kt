package com.xaarlox.task16_microgrid_gps_map.ui.components

import java.util.Locale

private val UA = Locale.forLanguageTag("uk-UA")

/** "850 м" for distances under a kilometer, "2,3 км" up to 100 km, and with no fractional part beyond that */
fun formatDistance(km: Double): String = when {
    km < 1.0 -> "${(km * 1000).toInt()} м"
    km < 100.0 -> String.format(UA, "%.1f км", km)
    else -> String.format(UA, "%,.0f км", km)
}

fun formatCoordinates(latitude: Double, longitude: Double): String =
    String.format(UA, "%.5f, %.5f", latitude, longitude)