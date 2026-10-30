package com.xaarlox.task12_microgrid_error_handling.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.data.network.AppError

/** Inline error state when there is nothing to display. The button appears only where retrying makes sense */
@Composable
fun ErrorState(
    error: AppError,
    attempts: Int,
    isLoading: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(error.title, style = MaterialTheme.typography.titleMedium)
            Text(
                error.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (attempts > 1) {
                Text(
                    "Виконано спроб: $attempts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (error.retryableByUser) {
                Button(onClick = onRetry, enabled = !isLoading, shape = RoundedCornerShape(14.dp)) {
                    Text("Повторити")
                }
            }
        }
    }
}