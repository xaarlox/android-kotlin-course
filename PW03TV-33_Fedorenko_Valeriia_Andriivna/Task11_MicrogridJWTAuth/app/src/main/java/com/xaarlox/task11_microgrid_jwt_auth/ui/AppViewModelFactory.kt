package com.xaarlox.task11_microgrid_jwt_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xaarlox.task11_microgrid_jwt_auth.AppContainer
import com.xaarlox.task11_microgrid_jwt_auth.ui.login.AuthViewModel
import com.xaarlox.task11_microgrid_jwt_auth.ui.main.PortalViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(
            container.authRepository,
            container.sessionManager
        )

        modelClass.isAssignableFrom(PortalViewModel::class.java) -> PortalViewModel(
            container.portalRepository,
            container.sessionManager
        )

        else -> error("Unknown ViewModel: ${modelClass.name}")
    } as T
}