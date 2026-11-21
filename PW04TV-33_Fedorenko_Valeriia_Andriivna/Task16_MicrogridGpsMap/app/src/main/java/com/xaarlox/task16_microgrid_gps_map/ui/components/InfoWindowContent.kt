package com.xaarlox.task16_microgrid_gps_map.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xaarlox.task16_microgrid_gps_map.domain.EnergyObject

private val InkPrimary = Color(0xFF1B1B1B)
private val InkSecondary = Color(0xFF5D4037)

/**
 * The content of the marker's InfoWindow: name, type, status, power and (if known) the distance from the user.
 * It is rendered into an image, so there are no animations or scrolling here
 */
@Composable
fun InfoWindowContent(item: EnergyObject, distanceKm: Double?) {
    // The status is tinted with a light palette — the window background is always light
    val statusTint = statusColor(item.status, dark = false)
    Column(Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
        Text(item.name, color = InkPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(item.type.title, color = InkSecondary, fontSize = 12.sp)
        Text(
            item.status.title,
            color = statusTint,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Text("${item.type.powerLabel}: ${item.powerText}", color = InkPrimary, fontSize = 12.sp)
        if (distanceKm != null) {
            Text("Від вас: ${formatDistance(distanceKm)}", color = InkSecondary, fontSize = 12.sp)
        }
    }
}