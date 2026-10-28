package com.xaarlox.task09_microgrid_repository.util

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

/**
 * Retries the request with an exponential backoff (300 ms → 600 ms → ...)
 * Only retries IOException: retrying programming errors is pointless
 */
suspend fun <T> retryWithBackoff(
    times: Int,
    initialDelayMs: Long = 300,
    block: suspend () -> T
): T {
    var pause = initialDelayMs
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {
            delay(pause.milliseconds)
            pause *= 2
        }
    }
    return block()
}