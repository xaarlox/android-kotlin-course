package com.xaarlox.task15_microgrid_filters.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task15_microgrid_filters.domain.EnergyType
import com.xaarlox.task15_microgrid_filters.domain.ListCriteria
import com.xaarlox.task15_microgrid_filters.domain.ObjectStatus
import com.xaarlox.task15_microgrid_filters.domain.PowerClass
import com.xaarlox.task15_microgrid_filters.domain.SortField

/**
 * Panel of multi-level filters and sorting.
 * Within a group the chips are combined with "OR", between groups — with "AND"
 */
@Composable
fun FilterPanel(
    criteria: ListCriteria,
    onToggleType: (EnergyType) -> Unit,
    onToggleStatus: (ObjectStatus) -> Unit,
    onTogglePowerClass: (PowerClass) -> Unit,
    onSortClick: (SortField) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ChipGroup("Тип об'єкта") {
            EnergyType.entries.forEach { type ->
                FilterChip(
                    selected = type in criteria.types,
                    onClick = { onToggleType(type) },
                    label = { Text(type.shortTitle) }
                )
            }
        }
        ChipGroup("Статус") {
            ObjectStatus.entries.forEach { status ->
                FilterChip(
                    selected = status in criteria.statuses,
                    onClick = { onToggleStatus(status) },
                    label = { Text(status.title) }
                )
            }
        }
        ChipGroup("Потужність / ємність, кВт · кВт·год") {
            PowerClass.entries.forEach { powerClass ->
                FilterChip(
                    selected = powerClass in criteria.powerClasses,
                    onClick = { onTogglePowerClass(powerClass) },
                    label = { Text(powerClass.title) }
                )
            }
        }
        ChipGroup("Сортування (повторний дотик змінює напрямок)") {
            SortField.entries.forEach { field ->
                val selected = criteria.sortField == field
                val arrow = if (!selected) "" else if (criteria.descending) " ↓" else " ↑"
                FilterChip(
                    selected = selected,
                    onClick = { onSortClick(field) },
                    label = { Text(field.title + arrow) }
                )
            }
        }
    }
}

/** A group label and a horizontal row of chips */
@Composable
private fun ChipGroup(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}