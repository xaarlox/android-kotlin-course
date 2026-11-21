package com.xaarlox.task13_microgrid_flow.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** "45,3 кВт": two decimal places are not needed — one is enough */
fun formatKw(value: Double): String =
    String.format(Locale.forLanguageTag("uk-UA"), "%.1f кВт", value)

fun formatPercent(value: Double): String =
    String.format(Locale.forLanguageTag("uk-UA"), "%.1f %%", value)

fun formatSigned(value: Double, unit: String): String =
    String.format(Locale.forLanguageTag("uk-UA"), "%+.1f %s", value, unit)

fun formatTime(timeMs: Long): String =
    SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("uk-UA")).format(Date(timeMs))