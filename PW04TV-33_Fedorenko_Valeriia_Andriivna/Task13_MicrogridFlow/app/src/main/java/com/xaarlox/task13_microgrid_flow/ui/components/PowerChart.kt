package com.xaarlox.task13_microgrid_flow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.task13_microgrid_flow.domain.HistoryPoint
import com.xaarlox.task13_microgrid_flow.ui.theme.GoldenYellow
import com.xaarlox.task13_microgrid_flow.ui.theme.GoldenYellowDark
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusLoad
import com.xaarlox.task13_microgrid_flow.ui.theme.StatusLoadDark

/** Real-time chart: generation and load over the latest measurements */
@Composable
fun PowerChart(history: List<HistoryPoint>, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    val generationColor = if (dark) GoldenYellowDark else GoldenYellow
    val loadColor = if (dark) StatusLoadDark else StatusLoad
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Генерація та навантаження",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Legend("Генерація", generationColor)
                Legend("Навантаження", loadColor)
            }
            Canvas(Modifier
                .fillMaxWidth()
                .height(140.dp)) {
                // Horizontal grid lines
                repeat(4) { i ->
                    val y = size.height * i / 3f
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }
                if (history.size < 2) return@Canvas

                val maxValue =
                    history.maxOf { maxOf(it.generationKw, it.loadKw) }.coerceAtLeast(10.0)
                val stepX = size.width / (history.size - 1)

                fun line(selector: (HistoryPoint) -> Double): Path = Path().apply {
                    history.forEachIndexed { index, point ->
                        val x = stepX * index
                        val y = size.height - (selector(point) / maxValue * size.height).toFloat()
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }

                drawPath(line { it.generationKw }, generationColor, style = Stroke(width = 4f))
                drawPath(line { it.loadKw }, loadColor, style = Stroke(width = 4f))
            }
        }
    }
}

@Composable
private fun Legend(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier
            .size(10.dp)
            .background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}