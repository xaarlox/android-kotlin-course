package com.xaarlox.pw01.task02.logic

// Parses user input as a finite, non-negative Double
// Rejects "Infinity"/"NaN" explicitly - toDoubleOrNull() accepts them, but they aren't valid physical inputs here
fun parsePositiveDouble(input: String): Double? {
    val value = input.toDoubleOrNull() ?: return null
    if (!value.isFinite()) return null
    return if (value >= 0.0) value else null
}

// Same as parsePositiveDouble, but also enforces an upper bound
fun parseBoundedDouble(input: String, max: Double): Double? {
    val value = parsePositiveDouble(input) ?: return null
    return if (value <= max) value else null
}