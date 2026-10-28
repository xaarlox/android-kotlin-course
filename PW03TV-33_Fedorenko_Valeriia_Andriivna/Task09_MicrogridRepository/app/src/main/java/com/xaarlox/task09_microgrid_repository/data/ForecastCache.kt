package com.xaarlox.task09_microgrid_repository.data

import com.xaarlox.task09_microgrid_repository.data.remote.ForecastDto

/**
 * In-memory cache for the forecast service response with a time-to-live (TTL)
 * The clock is passed as a parameter so tests do not have to wait for real seconds to pass
 * Not thread-safe on its own: the Repository calls it under a Mutex
 */
class ForecastCache(
    private val ttlMs: Long = 60_000L,
    private val now: () -> Long = System::currentTimeMillis
) {
    private var data: List<ForecastDto>? = null
    private var savedAt = 0L

    /** Fresh data, or null if the cache is empty or expired */
    fun getFresh(): List<ForecastDto>? = data?.takeIf { now() - savedAt < ttlMs }

    fun put(value: List<ForecastDto>) {
        data = value
        savedAt = now()
    }
}