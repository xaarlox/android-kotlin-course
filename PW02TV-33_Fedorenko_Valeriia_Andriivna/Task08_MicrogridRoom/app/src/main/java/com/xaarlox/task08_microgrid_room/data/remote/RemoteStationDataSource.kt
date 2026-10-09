package com.xaarlox.task08_microgrid_room.data.remote

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/** Station representation as returned by the remote server */
data class RemoteStationDto(
    val name: String, val powers: List<Double>, val voltages: List<Double>
)

/**
 * Remote data source contract. The Repository depends on this interface,
 * allowing the mock implementation to be swapped with a real Retrofit service
 * without affecting other layers
 */
interface RemoteStationDataSource {
    suspend fun fetchStations(): List<RemoteStationDto>
}

/**
 * Mock server implementation: simulates a 1.5-second network delay and random failures
 *
 * @param failureRate Failure probability between 0.0 and 1.0
 */
class FakeRemoteStationDataSource(private val failureRate: Double = 0.3) : RemoteStationDataSource {

    override suspend fun fetchStations(): List<RemoteStationDto> {
        delay(1500.milliseconds)
        if (Random.nextDouble() < failureRate) throw IOException("Сервер недоступний")
        return listOf(
            RemoteStationDto("Сонячна станція «Захід»", listOf(5.0, 4.2, 6.1), listOf(12.0, 12.4)),
            RemoteStationDto(
                "Вітрова станція «Північ»", listOf(2.5, 3.1), listOf(24.0, 23.5, 24.2)
            ),
            RemoteStationDto("Гібридна станція «Центр»", listOf(7.8, 6.4, 8.2), listOf(48.0, 47.6))
        )
    }
}