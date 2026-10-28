package com.xaarlox.task09_microgrid_repository.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import com.xaarlox.task09_microgrid_repository.domain.RecordSource
import com.xaarlox.task09_microgrid_repository.ui.formatKw

@Composable
fun RecordItem(record: PowerRecord, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    record.day.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(Modifier.weight(1f)) {
                Text(formatKw(record.powerKw), style = MaterialTheme.typography.titleMedium)
                Text(
                    if (record.source == RecordSource.FORECAST) "Прогноз" else "Вручну",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Видалити",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}