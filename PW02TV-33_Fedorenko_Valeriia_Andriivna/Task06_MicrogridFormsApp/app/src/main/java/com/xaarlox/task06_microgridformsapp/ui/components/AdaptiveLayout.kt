package com.xaarlox.task06_microgridformsapp.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val WideBreakpoint = 600.dp

/**
 * Adaptive layout: depending on available width, shows
 * a narrow (Column) or wide (Row) content variant
 *
 * @param compact content for narrow screens
 * @param wide content for wide screens
 * @param modifier external modifier
 */
@Composable
fun AdaptiveLayout(
    compact: @Composable () -> Unit,
    wide: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < WideBreakpoint) compact() else wide()
    }
}