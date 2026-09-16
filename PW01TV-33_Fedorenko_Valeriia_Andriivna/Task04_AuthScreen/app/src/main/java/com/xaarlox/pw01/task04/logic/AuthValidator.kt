package com.xaarlox.pw01.task04.logic

import com.xaarlox.pw01.task04.model.ValidationResult

private const val MIN_PASSWORD_LENGTH = 6

// Business logic kept separate from UI so it can be tested independently and reused between the login and registration screens
fun validateLogin(login: String, password: String): ValidationResult {
    val errors = mutableMapOf<String, String>()

    if (login.isBlank()) errors["login"] = "Введіть логін"
    if (password.isBlank()) errors["password"] = "Введіть пароль"
    else if (password.length < MIN_PASSWORD_LENGTH) {
        errors["password"] = "Пароль має містити щонайменше $MIN_PASSWORD_LENGTH символів"
    }

    return ValidationResult(isValid = errors.isEmpty(), fieldErrors = errors)
}

fun validateRegistration(
    name: String,
    email: String,
    password: String,
    confirmPassword: String
): ValidationResult {
    val errors = mutableMapOf<String, String>()

    if (name.isBlank()) errors["name"] = "Введіть ім'я"

    if (email.isBlank()) {
        errors["email"] = "Введіть електронну пошту"
    } else if (!isValidEmail(email)) {
        errors["email"] = "Некоректний формат електронної пошти"
    }

    if (password.isBlank()) {
        errors["password"] = "Введіть пароль"
    } else if (password.length < MIN_PASSWORD_LENGTH) {
        errors["password"] = "Пароль має містити щонайменше $MIN_PASSWORD_LENGTH символів"
    }

    if (confirmPassword != password) {
        errors["confirmPassword"] = "Паролі не збігаються"
    }

    return ValidationResult(isValid = errors.isEmpty(), fieldErrors = errors)
}

// Simple regex check - sufficient for client-side UX validation, not intended as a strict RFC-compliant email parser
private fun isValidEmail(email: String): Boolean {
    return Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email)
}