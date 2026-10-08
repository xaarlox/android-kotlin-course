package com.xaarlox.task06_microgridformsapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Input field with a helper text and an error message below it
 * If [error] is not null, the error is shown, otherwise — the [hint]
 *
 * @param label field label
 * @param value current value
 * @param onValueChange value change action
 * @param hint helper text below the field
 * @param error error text or null
 * @param modifier external modifier
 * @param keyboardType keyboard type
 * @param isPassword whether to mask the input
 * @param enabled whether the field is enabled (disabled during loading)
 */
@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            if (error != null) FieldErrorText(error) else FieldHint(hint)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    )
}