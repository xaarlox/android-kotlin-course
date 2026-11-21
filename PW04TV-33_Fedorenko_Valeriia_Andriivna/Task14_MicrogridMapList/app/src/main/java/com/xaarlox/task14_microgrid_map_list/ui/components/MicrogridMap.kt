package com.xaarlox.task14_microgrid_map_list.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xaarlox.task14_microgrid_map_list.domain.EnergyObject
import com.xaarlox.task14_microgrid_map_list.domain.EnergyType
import com.xaarlox.task14_microgrid_map_list.ui.map.MapGeometry
import com.xaarlox.task14_microgrid_map_list.ui.map.MapProjection
import com.xaarlox.task14_microgrid_map_list.ui.map.ScreenPoint
import kotlin.math.floor

private const val FOCUS_ZOOM = 2.4f // how many times the camera zooms in on the selected object
private const val GRID_STEP_KM = 2f

/**
 * A schematic microgrid map drawn on a Canvas using the real coordinates of the objects
 *
 * Two-way synchronization with the list:
 *  - in: [selectedId] — the camera smoothly flies to the selected marker, the marker grows and "pulses";
 *  - out: a tap on a marker → [onMarkerClick], a tap on an empty spot → [onMapClick]
 */
@Composable
fun MicrogridMap(
    objects: List<EnergyObject>,
    selectedId: String?,
    onMarkerClick: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    val projection = remember(objects) { MapProjection(objects) }
    val selected = objects.firstOrNull { it.id == selectedId }
    val focusPoint = selected?.let { projection.points[it.id] }

    val cameraSpec = tween<Float>(durationMillis = 700, easing = FastOutSlowInEasing)
    val cameraX by animateFloatAsState(
        focusPoint?.x ?: projection.centerX,
        cameraSpec,
        label = "cameraX"
    )
    val cameraY by animateFloatAsState(
        focusPoint?.y ?: projection.centerY,
        cameraSpec,
        label = "cameraY"
    )
    val zoom by animateFloatAsState(
        if (focusPoint != null) FOCUS_ZOOM else 1f,
        cameraSpec,
        label = "zoom"
    )
    val selection by animateFloatAsState(
        if (selected != null) 1f else 0f, tween(400), label = "selection"
    )
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Restart),
        label = "pulse"
    )

    val painters = EnergyType.entries.map { rememberVectorPainter(typeIcon(it)) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val landColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
    val lineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
    val highlightColor = MaterialTheme.colorScheme.primary
    val labelBg = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
    val labelStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    val scaleStyle = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentOnMapClick by rememberUpdatedState(onMapClick)

    Canvas(
        modifier
            .background(landColor)
            .pointerInput(objects, projection) {
                detectTapGestures { tap ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    val scale = projection.fitScale(w, h, FIT_PADDING_DP.dp.toPx()) * zoom
                    val markers = objects.mapNotNull { item ->
                        projection.points[item.id]?.let { p ->
                            item.id to MapGeometry.toScreen(p, cameraX, cameraY, scale, w, h)
                        }
                    }
                    val hit = MapGeometry.hitTest(
                        ScreenPoint(tap.x, tap.y),
                        markers,
                        HIT_RADIUS_DP.dp.toPx()
                    )
                    if (hit != null) currentOnMarkerClick(hit) else currentOnMapClick()
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val scale = projection.fitScale(w, h, FIT_PADDING_DP.dp.toPx()) * zoom

        fun screenOf(id: String): Offset? = projection.points[id]?.let {
            val s = MapGeometry.toScreen(it, cameraX, cameraY, scale, w, h)
            Offset(s.x, s.y)
        }

        val left = cameraX - w / 2f / scale
        val right = cameraX + w / 2f / scale
        val top = cameraY - h / 2f / scale
        val bottom = cameraY + h / 2f / scale
        var gx = floor(left / GRID_STEP_KM) * GRID_STEP_KM
        while (gx <= right) {
            val x = w / 2f + (gx - cameraX) * scale
            drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
            gx += GRID_STEP_KM
        }
        var gy = floor(top / GRID_STEP_KM) * GRID_STEP_KM
        while (gy <= bottom) {
            val y = h / 2f + (gy - cameraY) * scale
            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            gy += GRID_STEP_KM
        }

        val hub = objects.firstOrNull { it.type == EnergyType.SUBSTATION }
        val hubPos = hub?.let { screenOf(it.id) }
        if (hub != null && hubPos != null) {
            val dash = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
            objects.filter { it.id != hub.id }.forEach { item ->
                val pos = screenOf(item.id) ?: return@forEach
                val active = selected != null && (selected.id == item.id || selected.id == hub.id)
                drawLine(
                    color = if (active) highlightColor else lineColor,
                    start = hubPos,
                    end = pos,
                    strokeWidth = if (active) 4f else 2f,
                    pathEffect = dash
                )
            }
        }

        val baseRadius = MARKER_RADIUS_DP.dp.toPx()
        val ordered = objects.sortedBy { it.id == selectedId }
        ordered.forEach { item ->
            val pos = screenOf(item.id) ?: return@forEach
            val isSelected = item.id == selectedId
            val color = typeColor(item.type, dark)
            val radius = if (isSelected) baseRadius * (1f + 0.45f * selection) else baseRadius

            if (isSelected) {
                // A pulsing ring around the selected marker
                drawCircle(
                    color = color.copy(alpha = 0.45f * (1f - pulse)),
                    radius = radius + pulse * 22.dp.toPx(),
                    center = pos
                )
            }
            drawCircle(Color.White, radius + 3.dp.toPx(), pos)
            drawCircle(color, radius, pos)
            if (isSelected) {
                drawCircle(
                    highlightColor,
                    radius + 3.dp.toPx(),
                    pos,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            val iconSize = radius * 1.15f
            translate(left = pos.x - iconSize / 2f, top = pos.y - iconSize / 2f) {
                with(painters[item.type.ordinal]) {
                    draw(
                        size = Size(iconSize, iconSize),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            // The label is only for the selected marker, so the map is not cluttered
            if (isSelected) {
                val layout = textMeasurer.measure(item.name, labelStyle)
                val boxW = layout.size.width + 16.dp.toPx()
                val boxH = layout.size.height + 8.dp.toPx()
                val boxX = (pos.x - boxW / 2f).coerceIn(
                    4.dp.toPx(),
                    (w - boxW - 4.dp.toPx()).coerceAtLeast(4.dp.toPx())
                )
                val boxY = pos.y + radius + 10.dp.toPx()
                drawRoundRect(
                    color = labelBg,
                    topLeft = Offset(boxX, boxY),
                    size = Size(boxW, boxH),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
                drawText(layout, topLeft = Offset(boxX + 8.dp.toPx(), boxY + 4.dp.toPx()))
            }
        }

        val niceKm = listOf(0.25f, 0.5f, 1f, 2f, 5f, 10f)
            .lastOrNull { it * scale <= 90.dp.toPx() } ?: 0.25f
        val barW = niceKm * scale
        val barX = 12.dp.toPx()
        val barY = h - 14.dp.toPx()
        drawLine(lineColor, Offset(barX, barY), Offset(barX + barW, barY), strokeWidth = 3f)
        val kmText = textMeasurer.measure(
            if (niceKm < 1f) "${(niceKm * 1000).toInt()} м" else "${niceKm.toInt()} км",
            scaleStyle
        )
        drawText(kmText, topLeft = Offset(barX, barY - kmText.size.height - 3.dp.toPx()))
    }
}

private const val FIT_PADDING_DP = 44
private const val HIT_RADIUS_DP = 28
private const val MARKER_RADIUS_DP = 15