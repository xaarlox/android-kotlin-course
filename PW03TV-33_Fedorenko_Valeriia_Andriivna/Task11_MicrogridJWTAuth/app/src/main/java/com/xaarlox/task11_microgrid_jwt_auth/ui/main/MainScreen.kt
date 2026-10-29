package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xaarlox.task11_microgrid_jwt_auth.domain.Role
import com.xaarlox.task11_microgrid_jwt_auth.domain.Session
import com.xaarlox.task11_microgrid_jwt_auth.ui.login.AuthViewModel

/**
 * Access control in the UI: tabs and buttons unavailable to the role are simply not built.
 * This is purely for convenience (the role is taken from the token on the client). Real security is on the server: 403
 */
@Composable
fun MainScreen(
    session: Session,
    authViewModel: AuthViewModel,
    portalViewModel: PortalViewModel,
    snackbarHostState: SnackbarHostState
) {
    val state by portalViewModel.state.collectAsState()
    val isAdmin = session.role == Role.ADMIN
    val tabs = remember(isAdmin) { MainTab.entries.filter { isAdmin || !it.adminOnly } }
    var selected by rememberSaveable { mutableStateOf(MainTab.STATIONS) }
    val current = if (selected in tabs) selected else MainTab.STATIONS
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            portalViewModel.messageShown()
        }
    }

    Scaffold(
        containerColor = scheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = scheme.surface) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == current,
                        onClick = { selected = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = scheme.primaryContainer,
                            selectedIconColor = scheme.onPrimaryContainer,
                            selectedTextColor = scheme.onSurface
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (current) {
                MainTab.STATIONS -> StationsScreen(
                    state = state,
                    isAdmin = isAdmin,
                    onRefresh = portalViewModel::loadStations,
                    onToggle = portalViewModel::toggleStation
                )

                MainTab.USERS -> UsersScreen(
                    users = state.users,
                    onLoad = portalViewModel::loadUsers
                )

                MainTab.SESSION -> SessionScreen(
                    session = session,
                    probeResult = state.probeResult,
                    onAnonymous = portalViewModel::probeAnonymous,
                    onBrokenToken = portalViewModel::probeBrokenToken,
                    onAdminResource = portalViewModel::probeAdminResource,
                    onLogout = authViewModel::logout
                )
            }
        }
    }
}