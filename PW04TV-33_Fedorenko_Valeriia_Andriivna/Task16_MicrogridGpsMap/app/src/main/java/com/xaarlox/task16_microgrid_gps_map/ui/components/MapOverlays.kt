package com.xaarlox.task16_microgrid_gps_map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xaarlox.task16_microgrid_gps_map.domain.LocationState

/** A small "pill" over the map showing the GPS state */
@Composable
fun LocationStatusChip(location: LocationState, modifier: Modifier = Modifier) {
    val text = when (location) {
        LocationState.NoPermission -> "Геолокація вимкнена"
        LocationState.Searching -> "Пошук GPS…"
        is LocationState.Available -> {
            val p = location.point
            val accuracy = p.accuracyMeters?.let { " · ±${it.toInt()} м" } ?: ""
            formatCoordinates(p.latitude, p.longitude) + accuracy
        }

        is LocationState.Error -> "Помилка геолокації"
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/**
 * The banner shown when there is no location permission. The app keeps working:
 * the map and the list are available, just without the user's position and distances
 */
@Composable
fun PermissionBanner(
    permanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (permanentlyDenied) {
                    "Доступ до геолокації заборонено. Увімкніть його в налаштуваннях, щоб бачити себе на мапі."
                } else {
                    "Для відображення вашої позиції та відстаней потрібен доступ до геолокації."
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )
            Button(onClick = if (permanentlyDenied) onOpenSettings else onRequestPermission) {
                Text(if (permanentlyDenied) "Налаштування" else "Дозволити")
            }
        }
    }
}

/** Shown instead of the map if MAPS_API_KEY is not set: explains what to do */
@Composable
fun MissingKeyCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Мапа недоступна: не задано ключ Google Maps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                "Додайте в файл local.properties рядок  MAPS_API_KEY=ваш_ключ  " +
                        "і перезберіть проєкт (докладніше — SETUP_MAPS_KEY.md). " +
                        "Список об'єктів, фільтри й сортування працюють і без мапи.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}