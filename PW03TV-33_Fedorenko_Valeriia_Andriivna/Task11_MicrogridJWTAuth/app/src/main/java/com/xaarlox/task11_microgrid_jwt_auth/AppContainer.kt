package com.xaarlox.task11_microgrid_jwt_auth

import android.content.Context
import com.xaarlox.task11_microgrid_jwt_auth.data.AuthRepository
import com.xaarlox.task11_microgrid_jwt_auth.data.PortalRepository
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.TokenStorage
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.RetrofitProvider

class AppContainer(context: Context) {
    val sessionManager by lazy { SessionManager(TokenStorage(context)) }
    private val api by lazy { RetrofitProvider.create(sessionManager) }

    val authRepository by lazy { AuthRepository(api, sessionManager) }
    val portalRepository by lazy { PortalRepository(api) }
}