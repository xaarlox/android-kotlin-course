package com.xaarlox.task08_microgrid_room.domain

enum class PowerFilter { ALL, LOW, HIGH }

enum class SortOrder { NAME, POWER_DESC, VOLTAGE_DESC, NEWEST }

/**
 * Business logic for data processing: aggregation, filtering, and sorting
 * Architectural decision: these are pure functions with no database or UI access —
 * making them easy to unit test, while the ViewModel and Repository simply invoke them
 */
object StationAnalytics {
    const val HIGH_POWER_THRESHOLD_W = 5.0

    fun filterAndSort(
        stations: List<Station>,
        query: String,
        filter: PowerFilter,
        sort: SortOrder
    ): List<Station> {
        val q = query.trim()
        val filtered = stations.filter { station ->
            val matchesQuery = q.isEmpty() || station.name.contains(q, ignoreCase = true)
            val matchesFilter = when (filter) {
                PowerFilter.ALL -> true
                PowerFilter.LOW -> station.avgPower < HIGH_POWER_THRESHOLD_W
                PowerFilter.HIGH -> station.avgPower >= HIGH_POWER_THRESHOLD_W
            }
            matchesQuery && matchesFilter
        }
        return when (sort) {
            SortOrder.NAME -> filtered.sortedBy { it.name.lowercase() }
            SortOrder.POWER_DESC -> filtered.sortedByDescending { it.avgPower }
            SortOrder.VOLTAGE_DESC -> filtered.sortedByDescending { it.avgVoltage }
            SortOrder.NEWEST -> filtered.sortedByDescending { it.createdAt }
        }
    }

    fun aggregate(stations: List<Station>): GridStats {
        if (stations.isEmpty()) return GridStats.EMPTY
        val withVoltage = stations.filter { it.voltages.isNotEmpty() }
        return GridStats(
            stationCount = stations.size,
            totalPowerW = stations.sumOf { it.avgPower },
            avgVoltageV = if (withVoltage.isEmpty()) 0.0 else withVoltage.map { it.avgVoltage }
                .average(),
            peakStationName = stations.maxByOrNull { it.maxPower }?.name
        )
    }
}