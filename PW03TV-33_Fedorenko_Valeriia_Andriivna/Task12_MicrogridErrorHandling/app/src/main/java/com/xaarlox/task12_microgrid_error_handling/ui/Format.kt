package com.xaarlox.task12_microgrid_error_handling.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())

fun formatKw(value: Double): String = String.format(Locale.US, "%.1f кВт", value)
fun formatTime(ms: Long): String = timeFormatter.format(Instant.ofEpochMilli(ms))
fun formatSeconds(ms: Long): String = String.format(Locale.US, "%.1f с", ms / 1000.0)