package com.xaarlox.task08_microgrid_room.ui

import java.util.Locale

/** Formats a number with one decimal place using a dot as the decimal separator (matching user input format: 5.0) */
fun Double.format1(): String = String.format(Locale.US, "%.1f", this)

/** Formats a list of numbers as a comma-separated string: "5.0, 2.0" */
fun List<Double>.formatList(): String = joinToString(", ") { it.format1() }