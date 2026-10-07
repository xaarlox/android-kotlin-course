package com.xaarlox.task05_microgridnavapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xaarlox.task05_microgridnavapp.R
import com.xaarlox.task05_microgridnavapp.domain.PowerCalculator
import com.xaarlox.task05_microgridnavapp.ui.components.AdaptiveLayout
import com.xaarlox.task05_microgridnavapp.ui.components.AppButton
import com.xaarlox.task05_microgridnavapp.ui.components.AppHeader
import com.xaarlox.task05_microgridnavapp.ui.components.ScreenContainer

/**
 * Screen for entering the number of panels for the SPP power forecast
 * Accepts only digits; the button is enabled when a positive number is entered
 * The entered value survives screen rotation (rememberSaveable)
 *
 * @param userName user name for the greeting
 * @param onCalculate callback with a valid number of panels
 */
@Composable
fun ForecastInputScreen(userName: String, onCalculate: (Int) -> Unit) {
    var panelsInput by rememberSaveable { mutableStateOf("") }

    val panels = panelsInput.toIntOrNull()
    val isValid = panels != null && panels > 0
    val isError = panelsInput.isNotEmpty() && !isValid

    val onInputChange: (String) -> Unit = { new ->
        if (new.length <= PowerCalculator.MAX_PANELS_DIGITS && new.all { it.isDigit() }) {
            panelsInput = new
        }
    }
    val onSubmit = { if (isValid) onCalculate(panels!!) }

    ScreenContainer {
        AdaptiveLayout(
            compact = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ForecastIntroSection(userName)
                    Spacer(Modifier.height(8.dp))
                    ForecastFormSection(panelsInput, onInputChange, isError, isValid, onSubmit)
                }
            },
            wide = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ForecastIntroSection(userName, Modifier.weight(1f))
                    ForecastFormSection(
                        panelsInput, onInputChange, isError, isValid, onSubmit,
                        Modifier.weight(1f)
                    )
                }
            }
        )
    }
}

/**
 * Greeting and screen description block
 *
 * @param userName user name
 * @param modifier external modifier
 */
@Composable
private fun ForecastIntroSection(userName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AppHeader(text = stringResource(R.string.greeting, userName))
        Text(
            text = stringResource(R.string.forecast_subtitle),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

/**
 * Form block: number of panels field and calculate button
 *
 * @param value current text in the field
 * @param onValueChange text change action
 * @param isError whether to show a validation error
 * @param canSubmit whether the calculate button is enabled
 * @param onSubmit "Calculate power" click action
 * @param modifier external modifier
 */
@Composable
private fun ForecastFormSection(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.panels_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isError,
            supportingText = { if (isError) Text(stringResource(R.string.panels_error)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        AppButton(
            text = stringResource(R.string.calculate_button),
            onClick = onSubmit,
            enabled = canSubmit
        )
    }
}