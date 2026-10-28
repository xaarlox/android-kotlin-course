package com.xaarlox.task09_microgrid_repository.domain

data class PowerStats(
    val count: Int = 0,
    val totalKw: Double = 0.0,
    val averageKw: Double = 0.0,
    val peakDay: Int? = null,
    val peakKw: Double = 0.0
) {
    companion object {
        /** Pure business logic with no dependencies on Android, Room, or the network: easily unit-tested */
        fun from(records: List<PowerRecord>): PowerStats {
            if (records.isEmpty()) return PowerStats()
            val total = records.sumOf { it.powerKw }
            val peak = records.maxBy { it.powerKw }
            return PowerStats(
                count = records.size,
                totalKw = total,
                averageKw = total / records.size,
                peakDay = peak.day,
                peakKw = peak.powerKw
            )
        }
    }
}