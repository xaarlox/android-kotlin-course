package com.xaarlox.task16_microgrid_gps_map.domain

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** A geographic point: latitude/longitude in degrees, accuracy (error radius) in meters */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null
)

private const val EARTH_RADIUS_KM = 6371.0088

/**
 * The distance between two points on the Earth's surface by the haversine formula, km.
 * It does not depend on Android (unlike Location.distanceBetween), so it can be tested
 */
fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
}

fun EnergyObject.distanceKmFrom(point: GeoPoint): Double =
    distanceKm(point.latitude, point.longitude, latitude, longitude)

/** The state of the user's geolocation */
sealed interface LocationState {
    /** There is no permission (not granted or denied) */
    data object NoPermission : LocationState

    /** The permission is granted, waiting for the first coordinates (GPS is still "warming up") */
    data object Searching : LocationState

    data class Available(val point: GeoPoint) : LocationState

    data class Error(val message: String) : LocationState
}