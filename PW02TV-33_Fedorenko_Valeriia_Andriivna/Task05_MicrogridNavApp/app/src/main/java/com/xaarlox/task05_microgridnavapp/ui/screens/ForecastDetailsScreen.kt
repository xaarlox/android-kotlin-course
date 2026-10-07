package com.xaarlox.task05_microgridnavapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xaarlox.task05_microgridnavapp.R
import com.xaarlox.task05_microgridnavapp.domain.PowerCalculator
import com.xaarlox.task05_microgridnavapp.ui.components.AdaptiveLayout
import com.xaarlox.task05_microgridnavapp.ui.components.AppButton
import com.xaarlox.task05_microgridnavapp.ui.components.AppHeader
import com.xaarlox.task05_microgridnavapp.ui.components.InfoRow
import com.xaarlox.task05_microgridnavapp.ui.components.ScreenContainer

/**
 * Forecast details screen: number of panels and expected power
 * On narrow screens, data and buttons are arranged in a column;
 * on wide screens, they are side-by-side (Row)
 *
 * @param panelsCount number of panels passed via a navigation argument
 * @param onBackToForecast return to the input screen
 * @param onBackToMenu return to the main menu
 */
@Composable
fun ForecastDetailsScreen(
    panelsCount: Int,
    onBackToForecast: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val expectedPower = PowerCalculator.calculate(panelsCount)
    val powerText = stringResource(R.string.power_value, PowerCalculator.format(expectedPower))

    ScreenContainer {
        AdaptiveLayout(
            compact = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DetailsInfoSection(panelsCount, powerText)
                    Spacer(Modifier.height(48.dp))
                    DetailsActionsSection(onBackToForecast, onBackToMenu)
                }
            },
            wide = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DetailsInfoSection(panelsCount, powerText, Modifier.weight(1f))
                    DetailsActionsSection(onBackToForecast, onBackToMenu, Modifier.weight(1f))
                }
            }
        )
    }
}

/**
 * Forecast data block (header and two "label - value" rows)
 *
 * @param panelsCount number of panels
 * @param powerText formatted power with measurement units
 * @param modifier external modifier
 */
@Composable
private fun DetailsInfoSection(
    panelsCount: Int,
    powerText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AppHeader(text = stringResource(R.string.details_title))
        Spacer(Modifier.height(32.dp))
        InfoRow(label = stringResource(R.string.panels_count_label), value = "$panelsCount")
        Spacer(Modifier.height(16.dp))
        InfoRow(label = stringResource(R.string.expected_power_label), value = powerText)
    }
}

/**
 * Navigation buttons block for the details screen
 *
 * @param onBackToForecast "Return to forecast" action
 * @param onBackToMenu "Return to menu" action
 * @param modifier external modifier
 */
@Composable
private fun DetailsActionsSection(
    onBackToForecast: () -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppButton(text = stringResource(R.string.back_to_forecast), onClick = onBackToForecast)
        Spacer(Modifier.height(16.dp))
        AppButton(text = stringResource(R.string.back_to_menu), onClick = onBackToMenu)
    }
}