package com.xaarlox.task12_microgrid_error_handling.data.remote.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Failure scenarios for demonstration */
enum class FaultScenario(val label: String, val description: String) {
    NORMAL("Усе працює", "Сервер відповідає нормально"),
    OFFLINE("Немає з'єднання", "Перевірка мережі блокує запит ще до відправки"),
    TIMEOUT("Тайм-аут", "Сервер не відповідає: 3 спроби, потім помилка"),
    FLAKY("Тимчасовий збій", "Перші 2 спроби невдалі, 3-тя успішна (автоповтор спрацьовує)"),
    SERVER_500("HTTP 500", "Помилка сервера: 3 спроби, потім повідомлення"),
    SERVER_503("HTTP 503 + Retry-After", "Сервер просить почекати 1 с перед повтором"),
    RATE_LIMIT("HTTP 429", "Забагато запитів, Retry-After: 2 с"),
    NOT_FOUND("HTTP 404", "Ресурс не знайдено: без повторів"),
    UNAUTHORIZED("HTTP 401", "Потрібна автентифікація: діалог"),
    FORBIDDEN("HTTP 403", "Немає прав: діалог"),
    BAD_JSON("Пошкоджений JSON", "Код 200, але тіло відповіді не розібрати")
}

class FaultSimulator {
    private val _scenario = MutableStateFlow(FaultScenario.NORMAL)
    val scenario: StateFlow<FaultScenario> = _scenario.asStateFlow()

    private var flakyFailuresLeft = FLAKY_FAILURES

    fun select(scenario: FaultScenario) {
        synchronized(this) { flakyFailuresLeft = FLAKY_FAILURES }
        _scenario.value = scenario
    }

    /** true: this request should fail; after a series of failures, the next one will succeed and the counter is reset */
    @Synchronized
    fun consumeFlakyFailure(): Boolean {
        if (flakyFailuresLeft > 0) {
            flakyFailuresLeft--
            return true
        }
        flakyFailuresLeft = FLAKY_FAILURES
        return false
    }

    private companion object {
        const val FLAKY_FAILURES = 2
    }
}