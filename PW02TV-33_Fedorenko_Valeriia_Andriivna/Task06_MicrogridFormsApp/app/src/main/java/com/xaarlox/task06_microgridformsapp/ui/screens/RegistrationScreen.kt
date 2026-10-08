package com.xaarlox.task06_microgridformsapp.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task06_microgridformsapp.R
import com.xaarlox.task06_microgridformsapp.domain.FieldError
import com.xaarlox.task06_microgridformsapp.domain.FormField
import com.xaarlox.task06_microgridformsapp.domain.SourceType
import com.xaarlox.task06_microgridformsapp.ui.components.AdaptiveLayout
import com.xaarlox.task06_microgridformsapp.ui.components.AppButton
import com.xaarlox.task06_microgridformsapp.ui.components.FormHeader
import com.xaarlox.task06_microgridformsapp.ui.components.FormTextField
import com.xaarlox.task06_microgridformsapp.ui.components.SourceTypeSelector
import com.xaarlox.task06_microgridformsapp.ui.components.StatusArea
import com.xaarlox.task06_microgridformsapp.viewmodel.RegistrationUiState
import com.xaarlox.task06_microgridformsapp.viewmodel.RegistrationViewModel

/**
 * Energy source registration screen: connects [RegistrationViewModel] to the UI
 *
 * @param viewModel ViewModel containing the form state
 */
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = viewModel()) {
    RegistrationContent(
        state = viewModel.uiState,
        onSourceTypeChange = viewModel::onSourceTypeChange,
        onFieldChange = viewModel::onFieldChange,
        onSubmit = viewModel::submit
    )
}

/**
 * Stateless part of the screen: header, source type selection, fields, button, and status
 * The content is scrollable and width-constrained on large screens
 *
 * @param state current form state
 * @param onSourceTypeChange source type selection (solar or wind)
 * @param onFieldChange field value change action
 * @param onSubmit "Register" click action
 */
@Composable
private fun RegistrationContent(
    state: RegistrationUiState,
    onSourceTypeChange: (SourceType) -> Unit,
    onFieldChange: (FormField, String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
        ) {
            FormHeader(text = stringResource(state.sourceType.titleRes()))
            Spacer(Modifier.height(8.dp))
            SourceTypeSelector(
                selected = state.sourceType,
                onSelect = onSourceTypeChange,
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(16.dp))

            FormFields(state = state, onFieldChange = onFieldChange)

            Spacer(Modifier.height(8.dp))
            AppButton(
                text = stringResource(R.string.register_button),
                onClick = onSubmit,
                enabled = !state.isLoading
            )
            StatusArea(
                isLoading = state.isLoading,
                successMessage = if (state.isSuccess) stringResource(R.string.registration_success) else null,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Form fields group
 * On narrow screens, it uses a single column; on wide screens, two fields per row (Row)
 *
 * @param state current form state
 * @param onFieldChange field value change action
 */
@Composable
private fun FormFields(
    state: RegistrationUiState,
    onFieldChange: (FormField, String) -> Unit
) {
    val fields = FormField.entries
    AdaptiveLayout(
        compact = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                fields.forEach { field ->
                    RegistrationField(field, state, onFieldChange)
                }
            }
        },
        wide = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                fields.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        pair.forEach { field ->
                            RegistrationField(field, state, onFieldChange, Modifier.weight(1f))
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    )
}

/**
 * A single form field with a label, hint, and error
 *
 * @param field which field is being displayed
 * @param state current form state
 * @param onFieldChange field value change action
 * @param modifier external modifier
 */
@Composable
private fun RegistrationField(
    field: FormField,
    state: RegistrationUiState,
    onFieldChange: (FormField, String) -> Unit,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(field.labelRes()),
        value = state.valueOf(field),
        onValueChange = { onFieldChange(field, it) },
        hint = stringResource(field.hintRes(state.sourceType)),
        error = state.errors[field]?.let { stringResource(it.messageRes()) },
        keyboardType = field.keyboardType(),
        isPassword = field == FormField.PASSWORD,
        enabled = !state.isLoading,
        modifier = modifier
    )
}

/** Form header resource depending on the source type */
@StringRes
private fun SourceType.titleRes(): Int = when (this) {
    SourceType.SOLAR -> R.string.registration_title
    SourceType.WIND -> R.string.registration_title_wind
}

/** Field label resource */
@StringRes
private fun FormField.labelRes(): Int = when (this) {
    FormField.NAME -> R.string.field_name
    FormField.EMAIL -> R.string.field_email
    FormField.PASSWORD -> R.string.field_password
    FormField.POWER -> R.string.field_power
}

/** Hint resource; name and capacity depend on the source type */
@StringRes
private fun FormField.hintRes(type: SourceType): Int = when (this) {
    FormField.NAME ->
        if (type == SourceType.WIND) R.string.hint_name_wind else R.string.hint_name

    FormField.EMAIL -> R.string.hint_email
    FormField.PASSWORD -> R.string.hint_password
    FormField.POWER ->
        if (type == SourceType.WIND) R.string.hint_power_wind else R.string.hint_power
}

/** Keyboard type for the field */
private fun FormField.keyboardType(): KeyboardType = when (this) {
    FormField.NAME -> KeyboardType.Text
    FormField.EMAIL -> KeyboardType.Email
    FormField.PASSWORD -> KeyboardType.Password
    FormField.POWER -> KeyboardType.Decimal
}

/** Validation error text resource */
@StringRes
private fun FieldError.messageRes(): Int = when (this) {
    FieldError.EMPTY -> R.string.error_empty
    FieldError.INVALID_EMAIL -> R.string.error_invalid_email
    FieldError.EMAIL_TAKEN -> R.string.error_email_taken
    FieldError.WEAK_PASSWORD -> R.string.error_weak_password
    FieldError.INVALID_POWER -> R.string.error_invalid_power
}