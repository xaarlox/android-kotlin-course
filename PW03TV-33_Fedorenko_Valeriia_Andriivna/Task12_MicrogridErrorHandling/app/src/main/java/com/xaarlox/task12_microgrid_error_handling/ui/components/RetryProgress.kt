package com.xaarlox.task12_microgrid_error_handling.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task12_microgrid_error_handling.data.network.RetryInfo
import com.xaarlox.task12_microgrid_error_handling.ui.formatSeconds

/** The user sees that an auto-retry is in progress, rather than a "freeze" */
@Composable
fun RetryProgress(info: RetryInfo, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    AppCard(modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Тимчасова помилка. Повторна спроба ${info.nextAttempt} з ${info.maxAttempts} " +
                        "через ${formatSeconds(info.delayMs)}",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = scheme.primary,
                trackColor = scheme.outline.copy(alpha = 0.2f)
            )
        }
    }
}