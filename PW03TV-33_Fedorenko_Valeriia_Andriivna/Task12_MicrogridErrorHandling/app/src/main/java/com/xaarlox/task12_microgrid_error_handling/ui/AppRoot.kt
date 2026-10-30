package com.xaarlox.task12_microgrid_error_handling.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xaarlox.task12_microgrid_error_handling.ui.log.LogScreen
import com.xaarlox.task12_microgrid_error_handling.ui.log.LogViewModel
import com.xaarlox.task12_microgrid_error_handling.ui.simulator.SimulatorScreen
import com.xaarlox.task12_microgrid_error_handling.ui.stations.StationsScreen
import com.xaarlox.task12_microgrid_error_handling.ui.stations.StationsViewModel

@Composable
fun AppRoot(stationsViewModel: StationsViewModel, logViewModel: LogViewModel) {
    val stations by stationsViewModel.uiState.collectAsState()
    val log by logViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var tab by rememberSaveable { mutableStateOf(MainTab.STATIONS) }
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) { stationsViewModel.messages.collect { snackbarHostState.show(it) } }
    LaunchedEffect(Unit) { logViewModel.messages.collect { snackbarHostState.show(it) } }

    stations.dialog?.let { error ->
        AlertDialog(
            onDismissRequest = stationsViewModel::dismissDialog,
            confirmButton = {
                TextButton(onClick = stationsViewModel::dismissDialog) { Text("Зрозуміло") }
            },
            title = { Text(error.title) },
            text = { Text(error.message) }
        )
    }

    Scaffold(
        containerColor = scheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = scheme.surface) {
                MainTab.entries.forEach { item ->
                    NavigationBarItem(
                        selected = item == tab,
                        onClick = { tab = item },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
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
            when (tab) {
                MainTab.STATIONS -> StationsScreen(
                    state = stations,
                    onRefresh = stationsViewModel::refresh,
                    onToggle = stationsViewModel::toggleStation
                )

                MainTab.SIMULATOR -> SimulatorScreen(
                    selected = stations.scenario,
                    onSelect = stationsViewModel::selectScenario
                )

                MainTab.LOG -> LogScreen(
                    state = log,
                    onUpload = logViewModel::upload,
                    onClear = logViewModel::clear
                )
            }
        }
    }
}

private suspend fun SnackbarHostState.show(message: UiMessage) {
    val result = showSnackbar(
        message = message.text,
        actionLabel = if (message.retry != null) "Повторити" else null,
        duration = if (message.retry != null) SnackbarDuration.Long else SnackbarDuration.Short
    )
    if (result == SnackbarResult.ActionPerformed) message.retry?.invoke()
}