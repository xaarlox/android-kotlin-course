package com.xaarlox.task13_microgrid_flow.data

import com.xaarlox.task13_microgrid_flow.domain.BatteryParams
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import com.xaarlox.task13_microgrid_flow.domain.calculateBatteryPowerKw
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/**
 * Simulator of the microgrid's physical signals. Replaces real sensors and the forecast model
 *
 * A "day" lasts [DAY_MS] milliseconds so that changes are noticeable within a few dozen seconds.
 * All values are in kW, except the battery charge level (in percent)
 */
class SignalGenerator(
    private val random: Random = Random.Default,
    private val startMs: Long = System.currentTimeMillis()
) {
    private var cloudFactor = 0.85
    private var windKw = 18.0
    private var socPercent = 55.0

    /** The "ideal" (clear-sky) power of the solar panels at the moment [nowMs] */
    private fun clearSkySolar(nowMs: Long): Double {
        val progress = ((nowMs - startMs) % DAY_MS).toDouble() / DAY_MS
        return if (progress < DAYLIGHT_SHARE) {
            SOLAR_PEAK_KW * sin(PI * progress / DAYLIGHT_SHARE)
        } else {
            0.0
        }
    }

    /**
     * The next value of the [kind] sensor.
     * [balanceKw] is needed only by the battery: it charges or discharges depending on it
     */
    fun next(kind: SensorKind, nowMs: Long, balanceKw: Double): Double = when (kind) {
        SensorKind.SOLAR -> {
            // Cloudiness — a slow random walk between 0.35 and 1.0
            cloudFactor = (cloudFactor + random.nextDouble(-0.08, 0.08)).coerceIn(0.35, 1.0)
            max(0.0, clearSkySolar(nowMs) * cloudFactor + random.nextDouble(-1.0, 1.0))
        }

        // The forecast model does not know the real cloudiness — it assumes an average of 85%
        SensorKind.SOLAR_FORECAST ->
            max(0.0, clearSkySolar(nowMs) * FORECAST_CLOUD_AVERAGE + random.nextDouble(-0.5, 0.5))

        SensorKind.WIND -> {
            windKw = (windKw + random.nextDouble(-4.0, 4.0)).coerceIn(2.0, WIND_MAX_KW)
            windKw
        }

        SensorKind.LOAD -> {
            val progress = ((nowMs - startMs) % DAY_MS).toDouble() / DAY_MS
            val daily = 12.0 * sin(2 * PI * progress)
            (LOAD_BASE_KW + daily + random.nextDouble(-5.0, 5.0)).coerceIn(15.0, 80.0)
        }

        SensorKind.BATTERY -> {
            val hours = SensorKind.BATTERY.periodMs / 1000.0 * SIM_SPEED / 3600.0
            val energyKwh = calculateBatteryPowerKw(balanceKw, socPercent) * hours
            socPercent = (socPercent + energyKwh / BatteryParams.CAPACITY_KWH * 100.0)
                .coerceIn(BatteryParams.MIN_SOC, BatteryParams.MAX_SOC)
            socPercent
        }
    }

    companion object {
        const val DAY_MS = 120_000L
        const val DAYLIGHT_SHARE = 0.7
        const val SOLAR_PEAK_KW = 90.0
        const val FORECAST_CLOUD_AVERAGE = 0.85
        const val WIND_MAX_KW = 45.0
        const val LOAD_BASE_KW = 45.0
        const val SIM_SPEED = 120.0
    }
}