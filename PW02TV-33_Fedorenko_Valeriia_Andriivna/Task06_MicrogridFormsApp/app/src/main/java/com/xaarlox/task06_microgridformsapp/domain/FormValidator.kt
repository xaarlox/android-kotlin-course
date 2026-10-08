package com.xaarlox.task06_microgridformsapp.domain

/**
 * Business logic for comprehensive input data validation
 * Independent of Android/UI
 */
object FormValidator {
    const val MIN_PASSWORD_LENGTH = 8

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    /**
     * Validates a single field: non-empty value, email format,
     * password complexity, power is a number greater than 0
     *
     * @return error type or null if the value is valid
     */
    fun validate(field: FormField, value: String): FieldError? {
        if (value.isBlank()) return FieldError.EMPTY
        return when (field) {
            FormField.NAME -> null
            FormField.EMAIL ->
                if (EMAIL_REGEX.matches(value.trim())) null else FieldError.INVALID_EMAIL

            FormField.PASSWORD ->
                if (isStrongPassword(value)) null else FieldError.WEAK_PASSWORD

            FormField.POWER ->
                if (parsePower(value) != null) null else FieldError.INVALID_POWER
        }
    }

    fun validateAll(values: Map<FormField, String>): Map<FormField, FieldError> =
        FormField.entries
            .mapNotNull { field ->
                validate(field, values[field].orEmpty())?.let { field to it }
            }
            .toMap()

    fun isStrongPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() }

    fun parsePower(value: String): Double? =
        value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it > 0 }
}