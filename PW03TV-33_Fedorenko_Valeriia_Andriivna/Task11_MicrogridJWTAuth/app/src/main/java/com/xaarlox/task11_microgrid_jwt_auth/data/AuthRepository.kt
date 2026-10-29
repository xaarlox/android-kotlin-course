package com.xaarlox.task11_microgrid_jwt_auth.data

import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.InvalidTokenException
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.MicrogridApi
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.LoginRequest
import com.xaarlox.task11_microgrid_jwt_auth.domain.Session
import com.xaarlox.task11_microgrid_jwt_auth.util.runCatchingCancellable

/** Login: Retrofit receives the token, SessionManager validates its claims and stores it */
class AuthRepository(
    private val api: MicrogridApi,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<Session> =
        runCatchingCancellable {
            val token = api.login(LoginRequest(username.trim(), password)).token
                ?: throw InvalidTokenException()
            sessionManager.start(token).getOrThrow()
        }
}