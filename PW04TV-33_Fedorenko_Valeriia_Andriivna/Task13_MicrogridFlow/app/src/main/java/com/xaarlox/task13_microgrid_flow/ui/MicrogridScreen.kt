package com.xaarlox.task13_microgrid_flow.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import com.xaarlox.task13_microgrid_flow.domain.SourceStatus
import com.xaarlox.task13_microgrid_flow.ui.components.BalanceCard
import com.xaarlox.task13_microgrid_flow.ui.components.FaultsAndLogCard
import com.xaarlox.task13_microgrid_flow.ui.components.ForecastCard
import com.xaarlox.task13_microgrid_flow.ui.components.PowerChart
import com.xaarlox.task13_microgrid_flow.ui.components.SourceCard
import com.xaarlox.task13_microgrid_flow.ui.components.formatKw
import com.xaarlox.task13_microgrid_flow.ui.components.formatPercent

/**
 * The single screen. State is collected from the ViewModel via collectAsStateWithLifecycle():
 * collection stops automatically when the app goes to the background and resumes on return
 */
@Composable
fun MicrogridScreen(viewModel: MicrogridViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snapshot = state.snapshot

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth()) {
            Text(
                "Мікрогрід",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Просунутий рівень: комбінування потоків, retry, гарячі потоки",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BalanceCard(snapshot)
        PowerChart(state.history)

        // Four sources + forecast: values come from the latest snapshot / the zip pair
        SensorKind.entries.forEach { kind ->
            val valueText = when (kind) {
                SensorKind.SOLAR -> snapshot?.solarKw?.let(::formatKw)
                SensorKind.WIND -> snapshot?.windKw?.let(::formatKw)
                SensorKind.LOAD -> snapshot?.loadKw?.let(::formatKw)
                SensorKind.BATTERY -> snapshot?.batterySocPercent?.let(::formatPercent)
                SensorKind.SOLAR_FORECAST -> state.forecastAccuracy?.forecastKw?.let(::formatKw)
            } ?: "—"
            SourceCard(
                kind = kind,
                valueText = valueText,
                status = state.statuses[kind] ?: SourceStatus.Connecting,
                onInjectFault = { viewModel.injectFault(kind) }
            )
        }

        ForecastCard(state.forecastAccuracy)
        FaultsAndLogCard(
            events = state.events,
            randomFaultsEnabled = state.randomFaultsEnabled,
            onRandomFaultsChange = viewModel::setRandomFaults
        )
        Spacer(Modifier.height(16.dp))
    }
}