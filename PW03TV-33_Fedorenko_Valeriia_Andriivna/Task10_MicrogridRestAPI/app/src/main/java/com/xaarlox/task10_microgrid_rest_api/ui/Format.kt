package com.xaarlox.task10_microgrid_rest_api.ui

import java.util.Locale

fun formatKwh(value: Double): String = String.format(Locale.US, "%.1f кВт·год", value)