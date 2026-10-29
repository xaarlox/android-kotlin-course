package com.xaarlox.task11_microgrid_jwt_auth.data.auth

import com.xaarlox.task11_microgrid_jwt_auth.data.remote.InvalidTokenException
import com.xaarlox.task11_microgrid_jwt_auth.domain.Role
import com.xaarlox.task11_microgrid_jwt_auth.domain.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for authentication: the UI subscribes to [session] and automatically
 * switches between the login screen and the main screen. Anyone can terminate the session
 * (logout, timer, Interceptor on 401), while user notifications flow through a separate [notices] stream
 */
class SessionManager(
    private val storage: TokenStorage,
    private val now: () -> Long = System::currentTimeMillis
) {
    private val _session = MutableStateFlow(restore())
    val session: StateFlow<Session?> = _session.asStateFlow()

    private val _notices = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val notices: SharedFlow<String> = _notices.asSharedFlow()

    val currentToken: String? get() = _session.value?.token

    fun start(token: String): Result<Session> {
        val session = parse(token) ?: return Result.failure(InvalidTokenException())
        if (session.isExpired(now())) {
            return Result.failure(InvalidTokenException("Токен уже прострочений"))
        }
        storage.save(token)
        _session.value = session
        return Result.success(session)
    }

    fun end(reason: String? = null) {
        storage.clear()
        _session.value = null
        reason?.let { _notices.tryEmit(it) }
    }

    /** Token from the previous run: restored only if it is still valid */
    private fun restore(): Session? {
        val token = storage.read() ?: return null
        val session = parse(token)
        if (session == null || session.isExpired(now())) {
            storage.clear()
            return null
        }
        return session
    }

    private fun parse(token: String): Session? {
        val payload = JwtDecoder.decode(token) ?: return null
        val username = payload.sub?.takeIf { it.isNotBlank() } ?: return null
        val role = Role.fromClaim(payload.role) ?: return null
        val exp = payload.exp ?: return null
        return Session(username, role, token, exp * 1000)
    }
}