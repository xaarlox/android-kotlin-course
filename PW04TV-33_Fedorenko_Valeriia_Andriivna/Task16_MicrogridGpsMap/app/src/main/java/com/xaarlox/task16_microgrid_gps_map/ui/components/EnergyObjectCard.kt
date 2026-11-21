package com.xaarlox.task16_microgrid_gps_map.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task16_microgrid_gps_map.domain.EnergyObject

/**
 * Object card: name, type, status, power, distance from the user (if the GPS position is known)
 * and update date. The selected card (matching the highlighted marker on the map) expands:
 * description, coordinates and load
 */
@Composable
fun EnergyObjectCard(
    item: EnergyObject,
    selected: Boolean,
    distanceKm: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val dark = isSystemInDarkTheme()
    val accent = typeColor(item.type, dark)
    val statusTint = statusColor(item.status, dark)

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
            Modifier
                .padding(14.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // The badge: the object type, or a selection mark for the selected card
            Box(
                Modifier
                    .size(44.dp)
                    .background(if (selected) scheme.primary else accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selected) Icons.Filled.Place else typeIcon(item.type),
                    contentDescription = if (selected) "Вибрано на мапі" else item.type.title,
                    tint = if (selected) scheme.onPrimary else Color.White
                )
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurface
                    )
                    StatusBadge(item.status.title, statusTint)
                }
                Text(item.type.title, style = MaterialTheme.typography.labelLarge, color = accent)
                Text(
                    "${item.type.powerLabel}: ${item.powerText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSurface
                )
                if (distanceKm != null) {
                    Text(
                        "Відстань від вас: ${formatDistance(distanceKm)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = scheme.onSurface
                    )
                }
                Text(
                    "Оновлено: ${item.lastUpdateText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )

                if (selected) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        "Координати: %.4f° пн. ш., %.4f° сх. д.".format(
                            item.latitude,
                            item.longitude
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                    Text(
                        "Завантаження: ${item.loadPercent} %",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurface
                    )
                    LinearProgressIndicator(
                        progress = { item.loadPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = statusTint,
                        trackColor = scheme.surfaceVariant
                    )
                }
            }
        }
    }
}

/** A colored status "pill" */
@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.16f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}