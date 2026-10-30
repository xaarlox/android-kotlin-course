package com.xaarlox.task12_microgrid_error_handling.ui.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.ui.components.LogEntryCard
import com.xaarlox.task12_microgrid_error_handling.ui.components.ScreenHeader

@Composable
fun LogScreen(state: LogUiState, onUpload: () -> Unit, onClear: () -> Unit) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenHeader("Журнал помилок", "Записів: ${state.entries.size}") }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onUpload,
                    enabled = state.entries.isNotEmpty() && !state.isUploading,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isUploading) CircularProgressIndicator(
                        Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    else Text("Надіслати в аналітику")
                }
                TextButton(
                    onClick = onClear,
                    enabled = state.entries.isNotEmpty()
                ) { Text("Очистити") }
            }
        }
        if (state.entries.isEmpty()) {
            item {
                Text(
                    "Помилок не зафіксовано",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }
        items(state.entries) { LogEntryCard(it) }
    }
}