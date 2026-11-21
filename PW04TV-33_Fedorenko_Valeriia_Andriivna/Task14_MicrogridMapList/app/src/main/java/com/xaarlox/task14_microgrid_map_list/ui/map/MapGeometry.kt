package com.xaarlox.task14_microgrid_map_list.ui.map

import com.xaarlox.task14_microgrid_map_list.domain.EnergyObject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min

/** A point in world coordinates, km (x — east, y — south) */
data class WorldPoint(val x: Float, val y: Float)

/** A point in Canvas pixels */
data class ScreenPoint(val x: Float, val y: Float)

class MapProjection(objects: List<EnergyObject>) {

    /** World coordinates of every object by its id */
    val points: Map<String, WorldPoint>

    /** The center of the area that contains all objects (the "overview" camera) */
    val centerX: Float
    val centerY: Float

    private val spanX: Float
    private val spanY: Float

    init {
        if (objects.isEmpty()) {
            points = emptyMap()
            centerX = 0f; centerY = 0f; spanX = 1f; spanY = 1f
        } else {
            val lat0 = objects.map { it.latitude }.average()
            val lon0 = objects.map { it.longitude }.average()
            val kmPerDegLon = KM_PER_DEG_LAT * cos(lat0 * PI / 180.0)

            points = objects.associate {
                it.id to WorldPoint(
                    x = ((it.longitude - lon0) * kmPerDegLon).toFloat(),
                    y = ((lat0 - it.latitude) * KM_PER_DEG_LAT).toFloat()
                )
            }
            val xs = points.values.map { it.x }
            val ys = points.values.map { it.y }
            centerX = (xs.min() + xs.max()) / 2f
            centerY = (ys.min() + ys.max()) / 2f
            spanX = (xs.max() - xs.min()).coerceAtLeast(MIN_SPAN_KM)
            spanY = (ys.max() - ys.min()).coerceAtLeast(MIN_SPAN_KM)
        }
    }

    /** The scale (pixels per km) at which all objects fit in the Canvas with padding */
    fun fitScale(widthPx: Float, heightPx: Float, paddingPx: Float): Float {
        val availableW = (widthPx - 2 * paddingPx).coerceAtLeast(1f)
        val availableH = (heightPx - 2 * paddingPx).coerceAtLeast(1f)
        return min(availableW / spanX, availableH / spanY)
    }

    companion object {
        const val KM_PER_DEG_LAT = 110.57
        const val MIN_SPAN_KM = 0.5f
    }
}

object MapGeometry {

    /** World point → pixel, if the camera looks at (centerX, centerY) with the scale [scale] */
    fun toScreen(
        point: WorldPoint,
        centerX: Float,
        centerY: Float,
        scale: Float,
        widthPx: Float,
        heightPx: Float
    ): ScreenPoint = ScreenPoint(
        x = widthPx / 2f + (point.x - centerX) * scale,
        y = heightPx / 2f + (point.y - centerY) * scale
    )

    /**
     * Finds the marker hit by a tap: the nearest one within [radiusPx].
     * Returns its id, or null if the tap landed on an empty spot of the map
     */
    fun hitTest(
        tap: ScreenPoint,
        markers: List<Pair<String, ScreenPoint>>,
        radiusPx: Float
    ): String? =
        markers
            .map { (id, p) -> id to hypot(tap.x - p.x, tap.y - p.y) }
            .filter { (_, distance) -> distance <= radiusPx }
            .minByOrNull { (_, distance) -> distance }
            ?.first
}