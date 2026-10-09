package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Generic text input field with supporting hint and error display (used across forms and search)
 * Displays [error] if non-null; otherwise falls back to displaying [hint] (if provided)
 *
 * @param label Field label text
 * @param value Current input text
 * @param onValueChange Callback invoked when the input text changes
 * @param modifier External modifier to be applied to the field
 * @param hint Supporting hint text displayed below the field
 * @param error Error message to display, or null if there is no error
 * @param enabled Whether the input field is interactive and enabled
 */
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = when {
            error != null -> {
                { Text(error, color = MaterialTheme.colorScheme.error) }
            }

            hint != null -> {
                { Text(hint) }
            }

            else -> null
        },
        singleLine = true,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    )
}