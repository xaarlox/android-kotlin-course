package com.xaarlox.task14_microgrid_map_list.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task14_microgrid_map_list.domain.EnergyObject

/**
 * Energy object card (Card): name, type, description and power.
 * The selected card is highlighted with a border and a color — the same way as the marker on the map
 */
@Composable
fun EnergyObjectCard(
    item: EnergyObject,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val accent = typeColor(item.type, isSystemInDarkTheme())

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) scheme.primaryContainer.copy(alpha = 0.35f) else scheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) scheme.primary else scheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon(item.type), contentDescription = item.type.title, tint = Color.White)
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface
                )
                Text(
                    item.type.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = accent
                )
                Text(
                    item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
                Text(
                    "${item.type.powerLabel}: ${item.powerText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSurface
                )
            }
        }
    }
}