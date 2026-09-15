package com.xaarlox.pw01.task03.model

import androidx.compose.ui.graphics.vector.ImageVector

// id is used as a stable key for toggling state and for LazyVerticalGrid's `key` param
data class EnergySource(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val outputWatts: Int,
    val isActive: Boolean
)