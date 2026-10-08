package com.xaarlox.task07_microgrid_mvvm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task07_microgrid_mvvm.R
import com.xaarlox.task07_microgrid_mvvm.domain.EnergySource
import com.xaarlox.task07_microgrid_mvvm.ui.components.AdaptiveLayout
import com.xaarlox.task07_microgrid_mvvm.ui.components.AppButton
import com.xaarlox.task07_microgrid_mvvm.ui.components.PowerSourceCard
import com.xaarlox.task07_microgrid_mvvm.ui.components.SummaryCard
import com.xaarlox.task07_microgrid_mvvm.ui.titleRes
import com.xaarlox.task07_microgrid_mvvm.viewmodel.MicrogridViewModel
import com.xaarlox.task07_microgrid_mvvm.viewmodel.PowerUiState

/**
 * MVVM: View (screen). Subscribes to StateFlow from the ViewModel via collectAsState:
 * as soon as the ViewModel emits a new state, Compose automatically recomposes only the changed parts
 * User actions are delegated to the ViewModel via refresh / refreshAll calls
 *
 * @param viewModel microgrid ViewModel (created and retained by the system)
 */
@Composable
fun MicrogridDashboardScreen(viewModel: MicrogridViewModel = viewModel()) {
    val states by viewModel.states.collectAsState()
    val summary by viewModel.summary.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineSmall
            )

            SummaryCard(state = summary)

            AdaptiveLayout(
                compact = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        EnergySource.entries.forEach { source ->
                            SourceCard(
                                source,
                                states[source] ?: PowerUiState.Loading,
                                viewModel::refresh
                            )
                        }
                    }
                },
                wide = {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        EnergySource.entries.forEach { source ->
                            SourceCard(
                                source,
                                states[source] ?: PowerUiState.Loading,
                                viewModel::refresh,
                                Modifier.weight(1f)
                            )
                        }
                    }
                }
            )

            AppButton(
                text = stringResource(R.string.refresh_all_button),
                onClick = viewModel::refreshAll,
                enabled = states.values.any { it !is PowerUiState.Loading }
            )
        }
    }
}

/**
 * Specific source card: provides the name and notifies the ViewModel which exact source to refresh
 *
 * @param source energy source
 * @param state its current state
 * @param onRefresh refresh request (accepts the source)
 * @param modifier external modifier
 */
@Composable
private fun SourceCard(
    source: EnergySource,
    state: PowerUiState,
    onRefresh: (EnergySource) -> Unit,
    modifier: Modifier = Modifier
) {
    PowerSourceCard(
        title = stringResource(source.titleRes()),
        state = state,
        onRefresh = { onRefresh(source) },
        modifier = modifier
    )
}