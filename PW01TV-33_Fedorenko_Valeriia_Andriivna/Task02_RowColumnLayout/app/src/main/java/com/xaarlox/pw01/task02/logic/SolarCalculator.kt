package com.xaarlox.pw01.task02.logic

import com.xaarlox.pw01.task02.model.SolarCalculatorResult
import com.xaarlox.pw01.task02.model.SolarInputParams

/**
 * Calculates solar panel output power and daily energy yield
 *
 * power (W) = area (m^2) * irradiance (W/m^2) * efficiency (fraction)
 * dailyEnergy (kWh) = power (W) * peakSunHours / 1000
 */
fun calculateSolarGeneration(params: SolarInputParams): SolarCalculatorResult {
    val efficiencyFraction = params.efficiencyPercent / 100.0
    val power = params.areaM2 * params.irradianceWm2 * efficiencyFraction
    val dailyEnergy = (power * params.peakSunHours) / 1000.0

    return SolarCalculatorResult(
        powerWatts = power,
        dailyEnergyKwh = dailyEnergy
    )
}

/**
 * Recomputes daily energy across a range of peak-sun-hour values, keeping
 * all other parameters fixed
 *
 * Used to show a real chart of how the result would change under different sunlight conditions
 */
fun calculateSunHoursSensitivity(
    baseParams: SolarInputParams,
    sunHoursRange: List<Double>
): List<Double> =
    sunHoursRange.map { hours ->
        calculateSolarGeneration(baseParams.copy(peakSunHours = hours)).dailyEnergyKwh
    }