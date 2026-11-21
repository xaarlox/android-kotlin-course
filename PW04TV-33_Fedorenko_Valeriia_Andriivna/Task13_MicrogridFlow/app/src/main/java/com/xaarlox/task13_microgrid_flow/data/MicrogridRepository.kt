package com.xaarlox.task13_microgrid_flow.data

import com.xaarlox.task13_microgrid_flow.domain.EventLevel
import com.xaarlox.task13_microgrid_flow.domain.ForecastAccuracy
import com.xaarlox.task13_microgrid_flow.domain.MicrogridSnapshot
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.zip

/**
 * Combines the hot sensor flows into domain-model flows
 *
 *  - [snapshots] — the `combine` operator: four flows with DIFFERENT periods (1.0 / 1.3 / 1.7 / 1.5 s).
 *  A new snapshot is built when ANY of them changes (the latest value of the rest is used).
 *  - [forecastAccuracy] — the `zip` operator: the "actual" and "forecast" flows with the same period
 *  are joined PAIRWISE (the n-th value with the n-th).
 */
class MicrogridRepository(private val simulator: MicrogridSimulator) {

    val snapshots: Flow<MicrogridSnapshot> = combine(
        simulator.stream(SensorKind.SOLAR),
        simulator.stream(SensorKind.WIND),
        simulator.stream(SensorKind.LOAD),
        simulator.stream(SensorKind.BATTERY)
    ) { solar, wind, load, battery ->
        MicrogridSnapshot(
            solarKw = solar.value,
            windKw = wind.value,
            loadKw = load.value,
            batterySocPercent = battery.value
        )
    }.withPipelineRecovery("combine")

    val forecastAccuracy: Flow<ForecastAccuracy> = simulator.stream(SensorKind.SOLAR)
        .zip(simulator.stream(SensorKind.SOLAR_FORECAST)) { actual, forecast ->
            ForecastAccuracy(actualKw = actual.value, forecastKw = forecast.value)
        }
        .withPipelineRecovery("zip")

    /**
     * The second line of defence (the first is retry on every sensor):
     * if the combining pipeline itself fails, it is restarted up to [MAX_PIPELINE_RETRIES] times,
     * after which the error is intercepted by `catch` and the flow completes without crashing the app
     */
    private fun <T> Flow<T>.withPipelineRecovery(name: String): Flow<T> =
        retryWhen { cause, attempt ->
            val canRetry = attempt < MAX_PIPELINE_RETRIES
            if (canRetry) {
                simulator.report(
                    EventLevel.WARNING,
                    "Конвеєр $name: ${cause.message ?: "помилка"}. Перезапуск ${attempt + 1}/$MAX_PIPELINE_RETRIES"
                )
                delay(PIPELINE_RETRY_DELAY_MS)
            }
            canRetry
        }.catch { cause ->
            simulator.report(
                EventLevel.WARNING,
                "Конвеєр $name зупинено: ${cause.message ?: "помилка"}"
            )
        }

    companion object {
        const val MAX_PIPELINE_RETRIES = 5L
        const val PIPELINE_RETRY_DELAY_MS = 1_000L
    }
}