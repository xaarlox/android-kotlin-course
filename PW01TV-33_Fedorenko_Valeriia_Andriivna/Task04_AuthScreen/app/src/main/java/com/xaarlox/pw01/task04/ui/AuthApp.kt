package com.xaarlox.pw01.task04.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xaarlox.pw01.task04.ui.screens.LoginScreen
import com.xaarlox.pw01.task04.ui.screens.RegisterScreen

// Which screen is currently shown
private enum class AuthScreenType { LOGIN, REGISTER }

@Composable
fun AuthApp() {
    var currentScreen by remember { mutableStateOf(AuthScreenType.LOGIN) }

    when (currentScreen) {
        AuthScreenType.LOGIN -> LoginScreen(
            onNavigateToRegister = { currentScreen = AuthScreenType.REGISTER }
        )

        AuthScreenType.REGISTER -> RegisterScreen(
            onNavigateToLogin = { currentScreen = AuthScreenType.LOGIN },
            onRegistered = { currentScreen = AuthScreenType.LOGIN }
        )
    }
}