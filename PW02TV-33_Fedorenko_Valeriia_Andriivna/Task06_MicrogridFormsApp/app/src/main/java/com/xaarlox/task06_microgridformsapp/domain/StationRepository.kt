package com.xaarlox.task06_microgridformsapp.domain

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Simulates a remote station registry: verifies email uniqueness
 * and "processes" the request with a delay (to demonstrate the loading state)
 */
class StationRepository {
    private val registeredEmails = mutableSetOf("operator@solar.ua", "admin@mail.com")

    suspend fun register(email: String): RegistrationResult {
        delay(PROCESSING_DELAY_MS.milliseconds)
        val normalized = email.trim().lowercase()
        if (normalized in registeredEmails) return RegistrationResult.EMAIL_TAKEN
        registeredEmails += normalized
        return RegistrationResult.SUCCESS
    }

    private companion object {
        const val PROCESSING_DELAY_MS = 2000L
    }
}