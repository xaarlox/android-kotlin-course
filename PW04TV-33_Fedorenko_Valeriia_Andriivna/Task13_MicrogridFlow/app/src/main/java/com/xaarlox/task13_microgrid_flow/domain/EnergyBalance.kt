package com.xaarlox.task13_microgrid_flow.domain

import kotlin.math.abs

/** Parameters of the microgrid's battery */
object BatteryParams {
    const val CAPACITY_KWH = 150.0
    const val MAX_POWER_KW = 30.0
    const val MIN_SOC = 10.0
    const val MAX_SOC = 100.0
}

/**
 * Battery power for the current balance (generation − load).
 * A positive value means charging, a negative one means discharging.
 * The battery does not charge above [BatteryParams.MAX_SOC] and does not discharge below [BatteryParams.MIN_SOC]
 */
fun calculateBatteryPowerKw(balanceKw: Double, socPercent: Double): Double {
    val limited = balanceKw.coerceIn(-BatteryParams.MAX_POWER_KW, BatteryParams.MAX_POWER_KW)
    return when {
        limited > 0 && socPercent >= BatteryParams.MAX_SOC -> 0.0
        limited < 0 && socPercent <= BatteryParams.MIN_SOC -> 0.0
        else -> limited
    }
}

enum class BalanceMode(val title: String) {
    SURPLUS("Профіцит: заряджання / експорт"),
    DEFICIT("Дефіцит: розряд / імпорт"),
    BALANCED("Баланс")
}

/**
 * A microgrid "snapshot" — the result of combining (combine) four independent flows
 */
data class MicrogridSnapshot(
    val solarKw: Double,
    val windKw: Double,
    val loadKw: Double,
    val batterySocPercent: Double
) {
    val generationKw: Double get() = solarKw + windKw

    /** Balance: how much generation is left after covering the load */
    val balanceKw: Double get() = generationKw - loadKw

    /** Battery power: "+" charging, "−" discharging */
    val batteryPowerKw: Double get() = calculateBatteryPowerKw(balanceKw, batterySocPercent)

    /** Exchange with the external grid: "+" export, "−" import */
    val gridKw: Double get() = balanceKw - batteryPowerKw

    val mode: BalanceMode
        get() = when {
            abs(balanceKw) < 1.0 -> BalanceMode.BALANCED
            balanceKw > 0 -> BalanceMode.SURPLUS
            else -> BalanceMode.DEFICIT
        }
}

/**
 * An "actual — forecast" pair of solar generation — the result of joining flows with the zip operator
 */
data class ForecastAccuracy(
    val actualKw: Double,
    val forecastKw: Double
) {
    val errorKw: Double get() = actualKw - forecastKw

    /** Deviation from the forecast in percent; null if the forecast is almost zero (night) */
    val errorPercent: Double? get() = if (forecastKw > 1.0) errorKw / forecastKw * 100.0 else null
}

/** A chart history point */
data class HistoryPoint(
    val generationKw: Double,
    val loadKw: Double
)