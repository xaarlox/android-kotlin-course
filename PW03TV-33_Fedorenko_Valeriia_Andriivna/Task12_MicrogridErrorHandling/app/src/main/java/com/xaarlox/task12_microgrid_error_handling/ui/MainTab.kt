package com.xaarlox.task12_microgrid_error_handling.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val title: String, val icon: ImageVector) {
    STATIONS("Станції", Icons.Default.Bolt),
    SIMULATOR("Симулятор", Icons.Default.Tune),
    LOG("Журнал", Icons.Default.BugReport)
}