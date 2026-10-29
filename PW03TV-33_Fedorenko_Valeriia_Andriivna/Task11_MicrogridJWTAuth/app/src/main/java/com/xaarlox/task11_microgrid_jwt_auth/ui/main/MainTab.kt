package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(val title: String, val icon: ImageVector, val adminOnly: Boolean = false) {
    STATIONS("Станції", Icons.Default.Bolt),
    USERS("Користувачі", Icons.Default.AdminPanelSettings, adminOnly = true),
    SESSION("Сесія", Icons.Default.Person)
}