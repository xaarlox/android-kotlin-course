package com.xaarlox.task09_microgrid_repository.ui

import java.util.Locale

fun formatKw(value: Double): String = String.format(Locale.US, "%.1f кВт", value)