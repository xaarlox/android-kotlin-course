package com.xaarlox.task11_microgrid_jwt_auth.data.remote

import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Signs outgoing requests with the current session token.
 * If the server responds with 401 to a request signed by US, the token is invalid or expired:
 * we terminate the session, and the UI automatically returns to the login screen.
 * Requests with an existing Authorization header or an X-Skip-Auth marker are left untouched,
 * so diagnostic calls do not log out the user
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val skip = original.header(SKIP_AUTH) != null
        val hasExplicitAuth = original.header("Authorization") != null
        val token = sessionManager.currentToken
        val signedByUs = !skip && !hasExplicitAuth && token != null

        val builder = original.newBuilder().removeHeader(SKIP_AUTH)
        if (signedByUs) builder.header("Authorization", "Bearer $token")

        val response = chain.proceed(builder.build())
        if (signedByUs && response.code == 401) {
            sessionManager.end("Сесію завершено: токен недійсний або прострочений. Увійдіть знову")
        }
        return response
    }

    companion object {
        const val SKIP_AUTH = "X-Skip-Auth"
    }
}