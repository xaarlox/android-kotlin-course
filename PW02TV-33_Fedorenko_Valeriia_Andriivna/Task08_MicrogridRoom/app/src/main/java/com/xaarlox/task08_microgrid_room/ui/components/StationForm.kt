package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.InputField
import com.xaarlox.task08_microgrid_room.ui.messageRes
import com.xaarlox.task08_microgrid_room.viewmodel.FormState

/**
 * Add station form: station name, comma-separated power and voltage inputs, and submit button
 * Contains no business logic — purely renders [form] and hoists events up to the caller (ViewModel)
 *
 * @param form Current form state.
 * @param onNameChange Callback invoked when the name input changes
 * @param onPowersChange Callback invoked when the power values input changes
 * @param onVoltagesChange Callback invoked when the voltage values input changes
 * @param onSubmit Callback invoked when "Add station with data" is clicked
 * @param modifier External modifier to be applied to the form
 */
@Composable
fun StationForm(
    form: FormState,
    onNameChange: (String) -> Unit,
    onPowersChange: (String) -> Unit,
    onVoltagesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.form_title),
            style = MaterialTheme.typography.titleMedium
        )
        AppTextField(
            label = stringResource(R.string.field_name),
            value = form.name,
            onValueChange = onNameChange,
            hint = stringResource(R.string.hint_name),
            error = form.errors[InputField.NAME]?.let { stringResource(it.messageRes()) },
            enabled = !form.isSaving
        )
        AppTextField(
            label = stringResource(R.string.field_powers),
            value = form.powers,
            onValueChange = onPowersChange,
            hint = stringResource(R.string.hint_numbers),
            error = form.errors[InputField.POWERS]?.let { stringResource(it.messageRes()) },
            enabled = !form.isSaving
        )
        AppTextField(
            label = stringResource(R.string.field_voltages),
            value = form.voltages,
            onValueChange = onVoltagesChange,
            hint = stringResource(R.string.hint_numbers),
            error = form.errors[InputField.VOLTAGES]?.let { stringResource(it.messageRes()) },
            enabled = !form.isSaving
        )
        AppButton(
            text = stringResource(R.string.add_button),
            onClick = onSubmit,
            enabled = !form.isSaving
        )
    }
}