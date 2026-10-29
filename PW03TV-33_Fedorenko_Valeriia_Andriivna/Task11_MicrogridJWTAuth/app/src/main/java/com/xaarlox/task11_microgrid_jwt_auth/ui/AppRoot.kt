package com.xaarlox.task11_microgrid_jwt_auth.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.xaarlox.task11_microgrid_jwt_auth.ui.login.AuthViewModel
import com.xaarlox.task11_microgrid_jwt_auth.ui.login.LoginScreen
import com.xaarlox.task11_microgrid_jwt_auth.ui.main.MainScreen
import com.xaarlox.task11_microgrid_jwt_auth.ui.main.PortalViewModel

/**
 * UI root: session state determines what to display. No navigation library needed:
 * session present → main screen, no session (logout, 401, expiration) → login screen
 */
@Composable
fun AppRoot(authViewModel: AuthViewModel, portalViewModel: PortalViewModel) {
    val session by authViewModel.session.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        authViewModel.notices.collect { snackbarHostState.showSnackbar(it) }
    }

    val current = session
    if (current == null) {
        LoginScreen(authViewModel, snackbarHostState)
    } else {
        MainScreen(current, authViewModel, portalViewModel, snackbarHostState)
    }
}