package com.xaarlox.task10_microgrid_rest_api.util

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> retryWithBackoff(
    times: Int,
    initialDelayMs: Long = 300,
    shouldRetry: (Throwable) -> Boolean = { it is IOException },
    block: suspend () -> T
): T {
    var pause = initialDelayMs
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Throwable) {
            if (e is kotlin.coroutines.cancellation.CancellationException || !shouldRetry(e)) throw e
            delay(pause.milliseconds)
            pause *= 2
        }
    }
    return block()
}