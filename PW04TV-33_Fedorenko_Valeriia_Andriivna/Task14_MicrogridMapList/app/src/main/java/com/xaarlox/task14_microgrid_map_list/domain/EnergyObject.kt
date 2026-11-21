package com.xaarlox.task14_microgrid_map_list.domain

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
 * A microgrid energy object.
 * [latitude] / [longitude] — the geographic position for the map marker,
 * [power] — power (or capacity for a storage unit) in units of [EnergyType.unit]
 */
data class EnergyObject(
    val id: String,
    val name: String,
    val type: EnergyType,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val power: Double
) {
    val powerText: String get() = "${power.toInt()} ${type.unit}"
}