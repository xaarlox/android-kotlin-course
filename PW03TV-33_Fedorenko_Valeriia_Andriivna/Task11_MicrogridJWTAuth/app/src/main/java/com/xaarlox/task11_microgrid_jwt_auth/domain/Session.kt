package com.xaarlox.task11_microgrid_jwt_auth.domain

/** Session built from token claims. expiresAtMs: expiration time in ms */
data class Session(
    val username: String,
    val role: Role,
    val token: String,
    val expiresAtMs: Long
) {
    fun isExpired(nowMs: Long) = nowMs >= expiresAtMs
}