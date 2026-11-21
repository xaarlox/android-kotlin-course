package com.xaarlox.task16_microgrid_gps_map.domain

import java.text.Collator
import java.util.Locale

/** The field by which the list is sorted */
enum class SortField(val title: String) {
    NAME("Назва"),
    TYPE("Тип"),
    POWER("Потужність"),
    STATUS("Статус"),
    UPDATED("Оновлено"),
    DISTANCE("Відстань")
}

/**
 * All list display parameters in a single immutable object.
 *
 * The multi-level filtering rule:
 *  - between groups (search, type, status, power class) "AND" applies;
 *  - within a group — "OR" ("Sun" and "Wind" selected → both types are shown);
 *  - an empty group means "no restrictions"
 */
data class ListCriteria(
    val query: String = "",
    val types: Set<EnergyType> = emptySet(),
    val statuses: Set<ObjectStatus> = emptySet(),
    val powerClasses: Set<PowerClass> = emptySet(),
    val sortField: SortField = SortField.NAME,
    val descending: Boolean = false
) {
    /** How many filter groups are currently active (for the counter on the "Filters" button) */
    val activeFilterCount: Int
        get() = listOf(
            query.isNotBlank(),
            types.isNotEmpty(),
            statuses.isNotEmpty(),
            powerClasses.isNotEmpty()
        ).count { it }

    /** Resets filters and search, but keeps the chosen sorting */
    fun withoutFilters(): ListCriteria =
        copy(query = "", types = emptySet(), statuses = emptySet(), powerClasses = emptySet())
}

// Collator knows the Ukrainian alphabet (і, ї, є, ґ stand in their proper places, not by character codes)
private val collator: Collator = Collator.getInstance(Locale.forLanguageTag("uk-UA"))

private val byName = Comparator<EnergyObject> { a, b -> collator.compare(a.name, b.name) }

/** [user] is needed only for sorting by distance; without coordinates the order is by name */
private fun SortField.comparator(user: GeoPoint?): Comparator<EnergyObject> = when (this) {
    SortField.NAME -> byName
    SortField.TYPE -> compareBy<EnergyObject> { it.type.ordinal }
    SortField.POWER -> compareBy<EnergyObject> { it.power }
    SortField.STATUS -> compareBy<EnergyObject> { it.status.ordinal }
    SortField.UPDATED -> compareBy<EnergyObject> { it.lastUpdate }
    SortField.DISTANCE ->
        if (user == null) byName else compareBy<EnergyObject> { it.distanceKmFrom(user) }
}

/**
 * The screen's main pure function: search → filters → sorting.
 * [userLocation] — the user's current coordinates (for sorting by distance).
 * It does not depend on Android, so it is easy to test
 */
fun List<EnergyObject>.applyCriteria(
    criteria: ListCriteria,
    userLocation: GeoPoint? = null
): List<EnergyObject> {
    val query = criteria.query.trim()

    val filtered = filter { item ->
        (query.isEmpty() || item.name.contains(query, ignoreCase = true)) &&
                (criteria.types.isEmpty() || item.type in criteria.types) &&
                (criteria.statuses.isEmpty() || item.status in criteria.statuses) &&
                (criteria.powerClasses.isEmpty() || item.powerClass in criteria.powerClasses)
    }

    val primary = criteria.sortField.comparator(userLocation)
        .let { if (criteria.descending) it.reversed() else it }
    // Items equal by the main key always go by name — the order is stable and predictable
    return filtered.sortedWith(primary.then(byName))
}

/** Summary of the selected items. Power (kW) and capacity (kWh) are not mixed */
data class SelectionSummary(
    val count: Int = 0,
    val powerKw: Double = 0.0,
    val capacityKwh: Double = 0.0
)

fun List<EnergyObject>.summarize(): SelectionSummary = SelectionSummary(
    count = size,
    powerKw = filter { it.type != EnergyType.STORAGE }.sumOf { it.power },
    capacityKwh = filter { it.type == EnergyType.STORAGE }.sumOf { it.power }
)