package com.xaarlox.task12_microgrid_error_handling.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultScenario
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultSimulator
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Connectivity check BEFORE making a request (avoids wasting time on a guaranteed-to-fail call)
 * and a stream of network state changes (for the banner and auto-refresh).
 * The OFFLINE scenario from the simulator is also treated as no network access: this allows
 * showcasing offline mode during a defense without turning off Wi-Fi
 */
class ConnectivityChecker(context: Context, private val simulator: FaultSimulator) {

    private val manager = context.getSystemService(ConnectivityManager::class.java)

    fun isOnline(): Boolean = simulator.scenario.value != FaultScenario.OFFLINE && hasInternet()

    val online: Flow<Boolean> = combine(systemOnline(), simulator.scenario) { system, scenario ->
        system && scenario != FaultScenario.OFFLINE
    }.distinctUntilChanged()

    private fun hasInternet(): Boolean {
        val network = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun systemOnline(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(hasInternet())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(hasInternet())
            }
        }
        manager.registerDefaultNetworkCallback(callback)
        trySend(hasInternet())
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}