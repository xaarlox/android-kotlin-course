package com.xaarlox.task15_microgrid_filters.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task15_microgrid_filters.domain.SelectionSummary

/** Summary bar of the selected items: count, total power, bulk actions */
@Composable
fun SelectionBar(
    summary: SelectionSummary,
    onSelectAllVisible: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ) {
        Row(
            Modifier.padding(start = 14.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Вибрано: ${summary.count}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                val parts = buildList {
                    if (summary.powerKw > 0) add("${summary.powerKw.toInt()} кВт")
                    if (summary.capacityKwh > 0) add("${summary.capacityKwh.toInt()} кВт·год")
                }
                if (parts.isNotEmpty()) {
                    Text(
                        "Разом: " + parts.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            TextButton(onClick = onSelectAllVisible) { Text("Усі показані") }
            TextButton(onClick = onClear) { Text("Зняти") }
        }
    }
}