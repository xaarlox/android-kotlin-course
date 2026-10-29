package com.xaarlox.task10_microgrid_rest_api.domain

import kotlin.math.PI
import kotlin.math.pow

/** Station parameters (can be extracted to settings if needed) */
data class StationConfig(
    val panelAreaM2: Double = 200.0,
    val panelEfficiency: Double = 0.20,
    val performanceRatio: Double = 0.75,   // losses: inverter, cables, heating
    val rotorDiameterM: Double = 10.0,
    val powerCoefficient: Double = 0.40,   // Cp, theoretical Betz limit ≈ 0.59
    val ratedPowerKw: Double = 15.0,
    val cutInMs: Double = 3.0,
    val cutOutMs: Double = 25.0
)

/**
 * Business logic without Android/network dependencies: easily covered by unit tests.
 * This is an ESTIMATE, not a precise calculation: sufficient for an academic project
 */
object EnergyEstimator {
    private const val AIR_DENSITY = 1.225
    private const val MJ_PER_KWH = 3.6
    private const val AVG_TO_MAX_WIND =
        0.6    // the service provides the daily maximum, not the average

    /** E = (radiation, kWh/m²) × area × efficiency × performance ratio */
    fun solarKwh(radiationMjPerM2: Double, c: StationConfig): Double =
        radiationMjPerM2 / MJ_PER_KWH * c.panelAreaM2 * c.panelEfficiency * c.performanceRatio

    /** P = 0.5·ρ·A·Cp·v³, capped at turbine rated power; outside [cut-in, cut-out] the turbine is stopped */
    fun windKwh(maxWindMs: Double, c: StationConfig): Double {
        val v = maxWindMs * AVG_TO_MAX_WIND
        if (v < c.cutInMs || v > c.cutOutMs) return 0.0
        val area = PI * (c.rotorDiameterM / 2).pow(2)
        val powerKw = (0.5 * AIR_DENSITY * area * c.powerCoefficient * v.pow(3) / 1000.0)
            .coerceAtMost(c.ratedPowerKw)
        return powerKw * 24
    }
}