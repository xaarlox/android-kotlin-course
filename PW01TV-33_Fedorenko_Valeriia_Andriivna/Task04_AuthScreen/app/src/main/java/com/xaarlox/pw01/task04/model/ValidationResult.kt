package com.xaarlox.pw01.task04.model

// Represents the outcome of validating a form: either no problems, or a map of field name
data class ValidationResult(
    val isValid: Boolean,
    val fieldErrors: Map<String, String> = emptyMap()
)
