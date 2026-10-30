package com.xaarlox.task12_microgrid_error_handling.data.network

import kotlin.math.pow
import kotlin.random.Random

/**
 * Exponential backoff: 500 ms → 1 s → 2 s ... with ±20% jitter,
 * preventing thousands of clients from hammering the server simultaneously after a shared outage.
 * If the server specifies a retry delay (Retry-After), we respect it, but cap it at maxDelayMs
 */
data class RetryPolicy(
    val maxAttempts: Int = 3,
    val baseDelayMs: Long = 500,
    val maxDelayMs: Long = 5_000,
    val jitter: Double = 0.2
) {
    fun delayFor(attempt: Int, error: AppError): Long {
        val serverHint = (error as? AppError.Http)?.retryAfterMs
        if (serverHint != null) return serverHint.coerceAtMost(maxDelayMs)

        val exponential = (baseDelayMs * 2.0.pow(attempt - 1)).toLong().coerceAtMost(maxDelayMs)
        val spread = (exponential * jitter).toLong()
        return exponential + Random.nextLong(-spread, spread + 1)
    }
}