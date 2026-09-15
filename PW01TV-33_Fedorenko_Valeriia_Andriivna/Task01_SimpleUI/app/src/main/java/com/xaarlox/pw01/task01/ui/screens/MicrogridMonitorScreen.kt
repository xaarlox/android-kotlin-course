package com.xaarlox.pw01.task01.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xaarlox.pw01.task01.ui.theme.screenBackgroundForLevel
import kotlin.random.Random

@Composable
fun MicrogridMonitorScreen() {
    // mutableFloatStateOf: a Compose State holder optimized for primitive Float values
    // remember: keeps this State alive across recompositions, so the value survives when the UI redraws but is reset if the composable leaves the screen
    var targetPowerWatts by remember { mutableFloatStateOf(650f) }
    var targetBatteryPercent by remember { mutableFloatStateOf(85f) }

    // animateFloatAsState: automatically animates from the current displayed value to the new "target" value whenever the target changes
    val animatedPower by animateFloatAsState(
        targetValue = targetPowerWatts,
        animationSpec = tween(durationMillis = 800),
        label = "PowerAnimation"
    )

    val animatedBattery by animateFloatAsState(
        targetValue = targetBatteryPercent,
        animationSpec = tween(durationMillis = 800),
        label = "BatteryAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // screenBackgroundForLevel: picks a background color shade based on the screen's "difficulty level" and current light/dark theme
            .background(screenBackgroundForLevel(3)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Розумний мікрогрід (EMS)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // .toInt() drops the decimal part just for display purposes; the underlying animated value stays a Float
                    Text(
                        text = "Генерація PV:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${animatedPower.toInt()} Вт",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Накопичувач (BESS):",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${animatedBattery.toInt()} %",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // LinearProgressIndicator expects progress in the 0f..1f range, so we convert the 0..100 percentage into a fraction
                    // The color switches to "error" below 25% to warn about low charge
                    LinearProgressIndicator(
                        progress = { animatedBattery / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (animatedBattery > 25f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        // Simulates new sensor readings with random values
                        // Changing these State variables triggers recomposition and starts the animations defined above
                        targetPowerWatts = Random.nextInt(100, 2000).toFloat()
                        targetBatteryPercent = Random.nextInt(10, 100).toFloat()
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Симулювати оновлення EMS",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}