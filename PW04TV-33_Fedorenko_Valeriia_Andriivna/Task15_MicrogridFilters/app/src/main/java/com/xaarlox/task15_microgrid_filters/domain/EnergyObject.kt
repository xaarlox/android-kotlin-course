package com.xaarlox.task15_microgrid_filters.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** The type of a microgrid energy object */
enum class EnergyType(
    val title: String,
    val shortTitle: String,
    val powerLabel: String,
    val unit: String
) {
    SOLAR("Сонячна електростанція", "Сонце", "Встановлена потужність", "кВт"),
    WIND("Вітрова електростанція", "Вітер", "Встановлена потужність", "кВт"),
    STORAGE("Накопичувач енергії", "Накопичувач", "Ємність", "кВт·год"),
    CONSUMER("Споживач", "Споживач", "Пікове навантаження", "кВт"),
    SUBSTATION("Підстанція мікрогріду", "Підстанція", "Номінальна потужність", "кВт")
}

/**
 * Operating status. The declaration order = the sort order by status
 * (from "everything is fine" to "the worst")
 */
enum class ObjectStatus(val title: String) {
    WORKING("Працює"),
    STANDBY("Резерв"),
    MAINTENANCE("Обслуговування"),
    FAULT("Аварія")
}

/** Power class for the multi-level filter */
enum class PowerClass(val title: String) {
    LOW("до 50"),
    MEDIUM("50–100"),
    HIGH("понад 100");

    companion object {
        fun of(power: Double): PowerClass = when {
            power < 50.0 -> LOW
            power <= 100.0 -> MEDIUM
            else -> HIGH
        }
    }
}

/**
 * A microgrid energy object.
 * [power] — power (kW) or capacity (kWh) for a storage unit;
 * [loadPercent] — the current load; [lastUpdate] — the time of the last data update
 */
data class EnergyObject(
    val id: String,
    val name: String,
    val type: EnergyType,
    val status: ObjectStatus,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val power: Double,
    val loadPercent: Int,
    val lastUpdate: LocalDateTime
) {
    val powerText: String get() = "${power.toInt()} ${type.unit}"
    val lastUpdateText: String get() = lastUpdate.format(DATE_FORMAT)
    val powerClass: PowerClass get() = PowerClass.of(power)

    private companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    }
}