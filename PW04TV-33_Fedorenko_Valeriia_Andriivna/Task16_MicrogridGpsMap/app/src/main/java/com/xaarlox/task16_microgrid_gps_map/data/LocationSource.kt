package com.xaarlox.task16_microgrid_gps_map.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.xaarlox.task16_microgrid_gps_map.domain.GeoPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** The source of the user's coordinates. The interface allows replacing GPS in tests */
interface LocationSource {
    /**
     * A continuous flow of positions. Subscribing starts the updates, unsubscribing (cancelling the collection)
     * stops them automatically — this saves battery when the app is in the background
     */
    fun updates(): Flow<GeoPoint>
}

/**
 * The implementation on FusedLocationProviderClient (Google Play services): it picks the best
 * source on its own — GPS, Wi-Fi or the mobile network
 *
 * It may be called only after ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION is granted
 * (the ViewModel guarantees this); otherwise a SecurityException is thrown, which `catch` in the ViewModel intercepts
 */
class FusedLocationSource(context: Context) : LocationSource {

    private val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission") // the permission is checked by the ViewModel/screen
    override fun updates(): Flow<GeoPoint> = callbackFlow {
        // High-accuracy mode: updates every 5 s, but not more often than every 2 s
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(GeoPoint(location.latitude, location.longitude, location.accuracy))
                }
            }
        }

        // The last known position — so we do not wait for the first update
        client.lastLocation.addOnSuccessListener { location ->
            location?.let { trySend(GeoPoint(it.latitude, it.longitude, it.accuracy)) }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            .addOnFailureListener { close(it) } // a service error → the flow completes with an exception

        awaitClose { client.removeLocationUpdates(callback) }
    }

    private companion object {
        const val UPDATE_INTERVAL_MS = 5_000L
        const val FASTEST_INTERVAL_MS = 2_000L
    }
}