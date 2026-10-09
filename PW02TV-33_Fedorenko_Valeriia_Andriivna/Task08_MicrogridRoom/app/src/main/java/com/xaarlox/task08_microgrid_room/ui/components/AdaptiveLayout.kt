package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val WideBreakpoint = 600.dp

/**
 * Adaptive layout: displays either a compact or wide layout based on the available width
 *
 * @param compact Content composable for compact/narrow screens
 * @param wide Content composable for expanded/wide screens
 * @param modifier External modifier to be applied to the layout
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