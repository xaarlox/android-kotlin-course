package com.xaarlox.task08_microgrid_room.domain

enum class InputField { NAME, POWERS, VOLTAGES }

enum class InputError { NAME_EMPTY, NUMBERS_INVALID, DUPLICATE_NAME }

sealed interface ParseResult {
    data class Valid(val draft: StationDraft) : ParseResult
    data class Invalid(val errors: Map<InputField, InputError>) : ParseResult
}

/**
 * Validation and parsing of user input strings. Pure logic with no Android dependencies
 * Numbers are comma-separated; decimals use dots (as in the example: 5.0, 2.0)
 */
object StationInputParser {
    fun parse(name: String, powersText: String, voltagesText: String): ParseResult {
        val errors = mutableMapOf<InputField, InputError>()
        if (name.isBlank()) errors[InputField.NAME] = InputError.NAME_EMPTY

        val powers = parseNumbers(powersText)
        if (powers == null) errors[InputField.POWERS] = InputError.NUMBERS_INVALID

        val voltages = parseNumbers(voltagesText)
        if (voltages == null) errors[InputField.VOLTAGES] = InputError.NUMBERS_INVALID

        return if (errors.isEmpty()) {
            ParseResult.Valid(StationDraft(name.trim(), powers!!, voltages!!))
        } else {
            ParseResult.Invalid(errors)
        }
    }

    fun parseNumbers(text: String): List<Double>? {
        val parts = text.split(',').map { it.trim() }
        if (parts.any { it.isEmpty() }) return null
        val values = parts.map { it.toDoubleOrNull() ?: return null }
        return values.takeIf { list -> list.all { it.isFinite() && it >= 0 } }
    }
}