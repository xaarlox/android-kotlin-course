package com.xaarlox.task08_microgrid_room.domain

/**
 * Domain model representing a station. Independent of Room, decoupling
 * the UI and business logic from the storage layer
 * Computed properties represent derived data calculated from measurements
 */
data class Station(
    val id: Long,
    val name: String,
    val powers: List<Double>,
    val voltages: List<Double>,
    val createdAt: Long
) {
    val avgPower: Double get() = if (powers.isEmpty()) 0.0 else powers.average()
    val maxPower: Double get() = powers.maxOrNull() ?: 0.0
    val avgVoltage: Double get() = if (voltages.isEmpty()) 0.0 else voltages.average()
}

/** Validated data for a new station (the result of successful form validation) */
data class StationDraft(
    val name: String,
    val powers: List<Double>,
    val voltages: List<Double>
)

/**
 * Summary metrics for the entire microgrid
 *
 * @property stationCount Total number of stations
 * @property totalPowerW Aggregate average power output, in W
 * @property avgVoltageV Average voltage across stations with voltage measurements, in V
 * @property peakStationName Name of the station with the highest single power reading
 */
data class GridStats(
    val stationCount: Int,
    val totalPowerW: Double,
    val avgVoltageV: Double,
    val peakStationName: String?
) {
    companion object {
        val EMPTY = GridStats(0, 0.0, 0.0, null)
    }
}