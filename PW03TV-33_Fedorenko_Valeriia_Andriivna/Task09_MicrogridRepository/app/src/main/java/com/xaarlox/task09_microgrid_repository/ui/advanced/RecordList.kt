package com.xaarlox.task09_microgrid_repository.ui.advanced

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import com.xaarlox.task09_microgrid_repository.ui.components.RecordItem

fun LazyListScope.recordItems(records: List<PowerRecord>, onDelete: (Long) -> Unit) {
    if (records.isEmpty()) {
        item(key = "empty") {
            Text(
                "Записів немає",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )
        }
    } else {
        items(records, key = { it.id }) { record ->
            RecordItem(record, onDelete = { onDelete(record.id) })
        }
    }
}