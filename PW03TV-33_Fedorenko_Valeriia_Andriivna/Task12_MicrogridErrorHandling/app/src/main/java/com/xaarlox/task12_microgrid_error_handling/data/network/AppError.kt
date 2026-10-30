package com.xaarlox.task12_microgrid_error_handling.data.network

/**
 * Unified error classification understood by the UI. Technical exceptions (IOException, HttpException,
 * JsonParseException, etc.) do not pass beyond the data layer: ErrorMapper converts them
 *
 * isTransient — whether an AUTOMATIC retry makes sense (the failure might resolve itself);
 * retryableByUser — whether to show a "Retry" button (pointless for 4xx errors);
 * isBlocking — whether a Dialog is needed instead of a Snackbar (the user must acknowledge it)
 */
sealed class AppError(
    val code: String,
    val title: String,
    val message: String,
    val isTransient: Boolean,
    val retryableByUser: Boolean = true,
    val isBlocking: Boolean = false
) {
    object NoConnection : AppError(
        "NO_CONNECTION", "Немає з'єднання",
        "Перевірте підключення до інтернету й повторіть спробу.",
        isTransient = false
    )

    object Timeout : AppError(
        "TIMEOUT", "Сервер не відповідає",
        "Час очікування відповіді вичерпано.",
        isTransient = true
    )

    object Network : AppError(
        "NETWORK", "Помилка мережі",
        "Не вдалося зв'язатися із сервером. Перевірте з'єднання.",
        isTransient = true
    )

    object InvalidData : AppError(
        "INVALID_DATA", "Некоректні дані",
        "Сервер повернув дані в неочікуваному форматі.",
        isTransient = false
    )

    object Unknown : AppError(
        "UNKNOWN", "Невідома помилка",
        "Сталася неочікувана помилка. Спробуйте ще раз.",
        isTransient = false
    )

    class Http(val status: Int, val retryAfterMs: Long? = null) : AppError(
        code = "HTTP_$status",
        title = httpTitle(status),
        message = httpMessage(status, retryAfterMs),
        isTransient = status in TRANSIENT_STATUSES,
        retryableByUser = status !in NON_RETRYABLE_STATUSES,
        isBlocking = status == 401 || status == 403
    )
}

/** The server returned 200, but the data is invalid (missing required fields, etc.) */
class InvalidDataException(message: String = "Сервер повернув неповні дані") : Exception(message)

// Statuses that usually resolve on their own: timeouts, rate limits, server-side failures
private val TRANSIENT_STATUSES = setOf(408, 429, 500, 502, 503, 504)

// Retrying the request is pointless: the issue lies in the request itself or permissions
private val NON_RETRYABLE_STATUSES = setOf(400, 401, 403, 404, 422)

private fun httpTitle(status: Int): String = when (status) {
    400, 422 -> "Некоректний запит"
    401 -> "Потрібна автентифікація"
    403 -> "Доступ заборонено"
    404 -> "Не знайдено"
    408 -> "Час очікування вичерпано"
    429 -> "Забагато запитів"
    500 -> "Помилка сервера"
    502, 503, 504 -> "Сервер недоступний"
    else -> "Помилка HTTP $status"
}

private fun httpMessage(status: Int, retryAfterMs: Long?): String = when (status) {
    400 -> "Сервер не прийняв запит. Перевірте дані й спробуйте ще раз."
    401 -> "Сесія недійсна або її термін минув. Увійдіть знову."
    403 -> "У вас немає прав для цієї дії."
    404 -> "Запитаний ресурс не знайдено."
    408 -> "Сервер не дочекався запиту. Спробуйте ще раз."
    422 -> "Сервер відхилив дані як некоректні."
    429 -> "Забагато запитів. " +
            (retryAfterMs?.let { "Спробуйте через ${it / 1000} с." } ?: "Спробуйте трохи пізніше.")

    500 -> "Внутрішня помилка сервера. Ми вже повторили спробу, але вона не допомогла."
    502, 503, 504 -> "Сервер тимчасово недоступний. Спробуйте пізніше."
    in 400..499 -> "Помилка запиту (HTTP $status)."
    in 500..599 -> "Помилка на сервері (HTTP $status)."
    else -> "Неочікувана відповідь сервера (HTTP $status)."
}