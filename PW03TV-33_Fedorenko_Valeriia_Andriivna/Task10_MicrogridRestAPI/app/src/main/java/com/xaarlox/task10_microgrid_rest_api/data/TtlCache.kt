package com.xaarlox.task10_microgrid_rest_api.data

/** In-memory cache with time-to-live (TTL); the key is the query (city). Called under the repository Mutex */
class TtlCache<K, V>(
    private val ttlMs: Long = 5 * 60_000L,
    private val now: () -> Long = System::currentTimeMillis
) {
    private val map = mutableMapOf<K, Pair<Long, V>>()

    fun getFresh(key: K): V? = map[key]?.takeIf { now() - it.first < ttlMs }?.second

    fun put(key: K, value: V) {
        map[key] = now() to value
    }
}