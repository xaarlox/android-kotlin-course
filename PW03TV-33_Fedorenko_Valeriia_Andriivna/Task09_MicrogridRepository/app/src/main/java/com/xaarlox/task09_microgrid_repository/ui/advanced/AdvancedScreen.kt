package com.xaarlox.task09_microgrid_repository.ui.advanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * "Advanced-level" screen: layout scaffolding and adaptive composition only
 *
 * - narrow screen: single scrollable column;
 * - wide screen (>= 600dp): controls on the left, list on the right
 */
@Composable
fun AdvancedScreen(viewModel: AdvancedViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (maxWidth >= 600.dp) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ControlsPanel(state, viewModel)
                    }
                    LazyColumn(Modifier.weight(1f)) {
                        recordItems(state.records, viewModel::deleteRecord)
                    }
                }
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    item { ControlsPanel(state, viewModel) }
                    recordItems(state.records, viewModel::deleteRecord)
                }
            }
        }
    }
}