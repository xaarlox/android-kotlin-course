package com.xaarlox.pw01.task02.model

data class SolarInputParams(
    val areaM2: Double,
    val irradianceWm2: Double,
    val efficiencyPercent: Double,
    val peakSunHours: Double
)

data class SolarCalculatorResult(
    val powerWatts: Double,
    val dailyEnergyKwh: Double
)