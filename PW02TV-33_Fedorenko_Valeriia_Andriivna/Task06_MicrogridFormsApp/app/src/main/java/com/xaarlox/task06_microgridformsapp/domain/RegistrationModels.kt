package com.xaarlox.task06_microgridformsapp.domain

/** Energy source registration form fields */
enum class FormField { NAME, EMAIL, PASSWORD, POWER }

/** Type of renewable energy source being registered in the microgrid */
enum class SourceType { SOLAR, WIND }

/** Validation error types */
enum class FieldError {
    EMPTY,
    INVALID_EMAIL,
    EMAIL_TAKEN,
    WEAK_PASSWORD,
    INVALID_POWER
}

/** Result of registering the source in the "registry" */
enum class RegistrationResult { SUCCESS, EMAIL_TAKEN }