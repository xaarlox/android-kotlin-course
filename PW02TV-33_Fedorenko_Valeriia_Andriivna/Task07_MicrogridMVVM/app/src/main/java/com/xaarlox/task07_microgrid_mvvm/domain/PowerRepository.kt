package com.xaarlox.task07_microgrid_mvvm.domain

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Power data source (MVVM: Model)
 * The ViewModel depends on the interface, so the mock can be easily replaced with a real service
 */
interface PowerRepository {
    suspend fun fetchPower(source: EnergySource): Result<Int>
}

/**
 * Simulated network request: 1.5s delay, random power,
 * and random failures (to demonstrate the error state in the UI)
 *
 * @param failureRate failure probability from 0.0 to 1.0
 */
class FakePowerRepository(private val failureRate: Double = 0.25) : PowerRepository {

    override suspend fun fetchPower(source: EnergySource): Result<Int> {
        delay(1500.milliseconds)
        return if (Random.nextDouble() < failureRate) {
            Result.failure(IOException("Сервер недоступний"))
        } else {
            Result.success(Random.nextInt(0, source.capacityKw + 1))
        }
    }
}