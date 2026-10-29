package com.xaarlox.task11_microgrid_jwt_auth.ui

import java.util.Locale

fun formatKw(value: Double): String = String.format(Locale.US, "%.1f кВт", value)

fun formatRemaining(ms: Long): String {
    val seconds = (ms / 1000).coerceAtLeast(0)
    return String.format(Locale.US, "%d:%02d", seconds / 60, seconds % 60)
}