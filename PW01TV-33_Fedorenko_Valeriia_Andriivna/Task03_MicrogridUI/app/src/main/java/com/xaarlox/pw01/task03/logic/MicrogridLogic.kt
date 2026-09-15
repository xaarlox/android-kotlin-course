package com.xaarlox.pw01.task03.logic

import com.xaarlox.pw01.task03.model.EnergySource

// Returns a new list with the matching source toggled, instead of mutating in place
fun toggleSourceState(sources: List<EnergySource>, sourceId: String): List<EnergySource> {
    return sources.map { source ->
        if (source.id == sourceId) {
            source.copy(isActive = !source.isActive)
        } else {
            source
        }
    }
}

fun calculateTotalOutput(sources: List<EnergySource>): Int {
    return sources.filter { it.isActive }.sumOf { it.outputWatts }
}

// Returns null when the threshold input isn't a valid number, so the UI can simply skip showing a warning instead of crashing
fun isBelowThreshold(totalOutput: Int, thresholdText: String): Boolean? {
    val threshold = thresholdText.toIntOrNull() ?: return null
    return totalOutput < threshold
}

fun formatPower(watts: Int): String {
    return if (watts >= 1000) {
        "%.2f кВт".format(watts / 1000.0)
    } else {
        "$watts Вт"
    }
}