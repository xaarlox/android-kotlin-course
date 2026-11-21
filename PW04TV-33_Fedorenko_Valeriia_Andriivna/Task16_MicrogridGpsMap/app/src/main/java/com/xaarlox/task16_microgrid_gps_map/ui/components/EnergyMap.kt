package com.xaarlox.task16_microgrid_gps_map.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.xaarlox.task16_microgrid_gps_map.domain.EnergyObject
import com.xaarlox.task16_microgrid_gps_map.domain.GeoPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val FOCUS_ZOOM = 14f
private const val CAMERA_ANIMATION_MS = 700
private val UserBlue = Color(0xFF1A73E8)

/**
 * A Google map with the energy object markers and the user's position
 *
 * Two-way synchronization with the list:
 *  - in: [selectedId] — the camera zooms to the marker, the marker grows, a circle
 *    appears around it and the InfoWindow opens;
 *  - out: a tap on a marker → [onMarkerClick], on an empty spot → [onMapClick]
 *
 * Markers are shown only for [objects] — that is, with the active filters taken into account
 */
@Composable
fun EnergyMap(
    objects: List<EnergyObject>,
    selectedId: String?,
    userLocation: GeoPoint?,
    distancesKm: Map<String, Double>,
    onMarkerClick: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val dark = isSystemInDarkTheme()
    val density = LocalDensity.current
    val primary = MaterialTheme.colorScheme.primary
    val scope = rememberCoroutineScope()

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(50.45, 30.52), 10f)
    }
    // The camera can be moved only after the map has loaded and has a size
    var mapLoaded by remember { mutableStateOf(false) }
    val selected = objects.firstOrNull { it.id == selectedId }
    val visibleKey = objects.joinToString(",") { it.id }

    /** Show all visible objects (with padding from the edges). */
    suspend fun fitAll() {
        try {
            when {
                objects.isEmpty() -> Unit
                objects.size == 1 -> cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(objects[0].latLng(), FOCUS_ZOOM),
                    CAMERA_ANIMATION_MS
                )

                else -> {
                    val bounds = LatLngBounds.builder()
                        .apply { objects.forEach { include(it.latLng()) } }
                        .build()
                    val padding = with(density) { 56.dp.roundToPx() }
                    cameraState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, padding),
                        CAMERA_ANIMATION_MS
                    )
                }
            }
        } catch (_: IllegalStateException) {
            // The map has no size yet (for example, during screen rotation) — skip the animation
        }
    }

    // Selection (from the list or the map) → camera on the object; no selection or filters changed → all objects
    LaunchedEffect(mapLoaded, selectedId, visibleKey) {
        if (!mapLoaded) return@LaunchedEffect
        if (selected != null) {
            cameraState.animate(
                CameraUpdateFactory.newLatLngZoom(selected.latLng(), FOCUS_ZOOM),
                CAMERA_ANIMATION_MS
            )
        } else {
            fitAll()
        }
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false
            ),
            onMapLoaded = { mapLoaded = true },
            onMapClick = { onMapClick() }
        ) {
            objects.forEach { item ->
                key(item.id) {
                    val isSelected = item.id == selectedId
                    val markerState = rememberMarkerState(key = item.id, position = item.latLng())
                    // Marker color = the type color in the legend and on the card
                    val icon = remember(item.type, isSelected, dark) {
                        markerIcon(
                            fill = typeColor(item.type, dark).toArgb(),
                            selected = isSelected,
                            ring = primary.toArgb(),
                            density = density.density
                        )
                    }

                    MarkerInfoWindowContent(
                        state = markerState,
                        icon = icon,
                        anchor = Offset(0.5f, 0.5f),
                        zIndex = if (isSelected) 2f else 1f,
                        onClick = {
                            onMarkerClick(item.id)
                            markerState.showInfoWindow()
                            true // we handled the event: the default marker "centering" is not needed
                        }
                    ) {
                        InfoWindowContent(item, distancesKm[item.id])
                    }

                    // The selected marker: open the InfoWindow and draw the highlight circle
                    LaunchedEffect(isSelected) {
                        if (isSelected) markerState.showInfoWindow()
                    }
                    if (isSelected) {
                        Circle(
                            center = item.latLng(),
                            radius = 350.0,
                            fillColor = primary.copy(alpha = 0.14f),
                            strokeColor = primary,
                            strokeWidth = 4f
                        )
                    }
                }
            }

            if (userLocation != null) {
                val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
                val userState = rememberMarkerState(key = "user", position = userLatLng)
                LaunchedEffect(userLatLng) { userState.position = userLatLng }
                val userIcon = remember { userDotIcon(UserBlue.toArgb(), density.density) }

                Circle(
                    center = userLatLng,
                    radius = (userLocation.accuracyMeters ?: 0f).toDouble().coerceAtLeast(10.0),
                    fillColor = UserBlue.copy(alpha = 0.12f),
                    strokeColor = UserBlue.copy(alpha = 0.5f),
                    strokeWidth = 2f
                )
                Marker(
                    state = userState,
                    icon = userIcon,
                    anchor = Offset(0.5f, 0.5f),
                    title = "Ви тут",
                    zIndex = 3f
                )
            }
        }

        Column(
            Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(onClick = { scope.launch { fitAll() } }, enabled = mapLoaded) {
                Icon(Icons.Filled.ZoomOutMap, contentDescription = "Показати всі об'єкти")
            }
            FilledTonalIconButton(
                onClick = {
                    userLocation?.let {
                        scope.launch {
                            cameraState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(it.latitude, it.longitude),
                                    FOCUS_ZOOM
                                ),
                                CAMERA_ANIMATION_MS
                            )
                        }
                    }
                },
                enabled = mapLoaded && userLocation != null
            ) {
                Icon(Icons.Filled.MyLocation, contentDescription = "Моя позиція")
            }
        }

        overlay()
    }
}

private fun EnergyObject.latLng() = LatLng(latitude, longitude)

/** A colored circle with a white outline; the selected marker is larger and has a ring in the theme color */
private fun markerIcon(fill: Int, selected: Boolean, ring: Int, density: Float): BitmapDescriptor {
    val sizeDp = if (selected) 34f else 24f
    val size = (sizeDp * density).roundToInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val center = size / 2f
    val stroke = (if (selected) 3f else 2f) * density

    paint.color = if (selected) ring else android.graphics.Color.WHITE
    canvas.drawCircle(center, center, center, paint)
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(center, center, center - stroke, paint)
    paint.color = fill
    canvas.drawCircle(center, center, center - stroke * 2f, paint)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/** A blue "You are here" dot with a white outline (like in Google Maps) */
private fun userDotIcon(color: Int, density: Float): BitmapDescriptor {
    val size = (22f * density).roundToInt()
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val center = size / 2f
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(center, center, center, paint)
    paint.color = color
    canvas.drawCircle(center, center, center - 3f * density, paint)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}