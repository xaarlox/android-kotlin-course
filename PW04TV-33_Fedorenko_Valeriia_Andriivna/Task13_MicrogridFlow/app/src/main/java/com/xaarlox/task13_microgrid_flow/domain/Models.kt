package com.xaarlox.task13_microgrid_flow.domain

/**
 * Types of microgrid data sources ("sensors").
 * [periodMs] — how often the sensor produces a new value.
 * The solar sensor and its forecast have the same period, so they are convenient
 * to combine with the zip operator (an "actual — forecast" pair)
 */
enum class SensorKind(val title: String, val unit: String, val periodMs: Long) {
    SOLAR("Сонячні панелі", "кВт", 1_000),
    SOLAR_FORECAST("Прогноз генерації сонця", "кВт", 1_000),
    WIND("Вітрогенератор", "кВт", 1_300),
    LOAD("Навантаження", "кВт", 1_700),
    BATTERY("Акумулятор (рівень заряду)", "%", 1_500)
}

/** A single measured value of a sensor */
data class SensorSample(
    val kind: SensorKind,
    val value: Double,
    val timestampMs: Long
)

/** Connection status of a single data source */
sealed interface SourceStatus {
    /** The flow has not produced any value yet */
    data object Connecting : SourceStatus

    /** Data is arriving normally */
    data object Online : SourceStatus

    /** An error occurred; the flow is reconnecting automatically */
    data class Reconnecting(val attempt: Int, val reason: String) : SourceStatus
}

enum class EventLevel { INFO, WARNING, SUCCESS }

/** An entry in the stream event log (for a clear demonstration of retry) */
data class StreamEvent(
    val id: Long,
    val timeMs: Long,
    val level: EventLevel,
    val message: String
)

/** Sensor read error: no signal or an invalid value */
class SensorException(val kind: SensorKind, message: String) : Exception(message)