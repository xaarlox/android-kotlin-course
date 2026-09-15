package com.xaarlox.pw01.task02.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.xaarlox.pw01.task02.logic.calculateSolarGeneration
import com.xaarlox.pw01.task02.logic.calculateSunHoursSensitivity
import com.xaarlox.pw01.task02.logic.parseBoundedDouble
import com.xaarlox.pw01.task02.logic.parsePositiveDouble
import com.xaarlox.pw01.task02.model.SolarCalculatorResult
import com.xaarlox.pw01.task02.model.SolarInputParams
import kotlinx.coroutines.launch

// Identifies each input parameter
private enum class SolarParam { AREA, IRRADIANCE, EFFICIENCY, SUN_HOURS }

private data class FieldDefinition(
    val id: SolarParam,
    val label: String,
    val maxValue: Double? = null
)

private val fieldDefinitions = listOf(
    FieldDefinition(SolarParam.AREA, "Площа панелі (м²)"),
    FieldDefinition(SolarParam.IRRADIANCE, "Інсоляція (Вт/м²)"),
    FieldDefinition(SolarParam.EFFICIENCY, "ККД панелі (%)", maxValue = 100.0),
    FieldDefinition(SolarParam.SUN_HOURS, "Пікові сонячні години/день", maxValue = 24.0)
)

@Composable
fun SolarCalculatorScreen() {
    val fieldValues = remember {
        mutableStateMapOf(
            SolarParam.AREA to "1.6",
            SolarParam.IRRADIANCE to "900",
            SolarParam.EFFICIENCY to "18",
            SolarParam.SUN_HOURS to "5"
        )
    }

    var result by remember { mutableStateOf<SolarCalculatorResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val chartModelProducer = remember { CartesianChartModelProducer() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Розрахунок сонячної генерації",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        fieldDefinitions.forEach { def ->
            OutlinedTextField(
                value = fieldValues[def.id] ?: "",
                onValueChange = { fieldValues[def.id] = it },
                label = { Text(def.label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Button(onClick = {
            // Parse each field, applying a bound where the definition specifies one
            val parsedValues = fieldDefinitions.associate { def ->
                val text = fieldValues[def.id] ?: ""
                val parsed = if (def.maxValue != null) {
                    parseBoundedDouble(text, def.maxValue)
                } else {
                    parsePositiveDouble(text)
                }
                def.id to parsed
            }

            if (parsedValues.values.any { it == null }) {
                errorMessage = "Перевірте, будь ласка, введені значення"
                return@Button
            }

            errorMessage = null
            val params = SolarInputParams(
                areaM2 = parsedValues.getValue(SolarParam.AREA)!!,
                irradianceWm2 = parsedValues.getValue(SolarParam.IRRADIANCE)!!,
                efficiencyPercent = parsedValues.getValue(SolarParam.EFFICIENCY)!!,
                peakSunHours = parsedValues.getValue(SolarParam.SUN_HOURS)!!
            )
            val calcResult = calculateSolarGeneration(params)
            result = calcResult

            coroutineScope.launch {
                chartModelProducer.runTransaction {
                    columnSeries {
                        // Real computed values: daily energy for a range of peak-sun-hour scenarios, not fabricated multipliers
                        val sunHoursRange = (1..7).map { it.toDouble() }
                        val sensitivity = calculateSunHoursSensitivity(params, sunHoursRange)
                        series(y = sensitivity)
                    }
                }
            }
        }) {
            Text("Розрахувати")
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        result?.let {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Потужність:")
                Text("%.1f Вт".format(it.powerWatts))
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добова генерація:")
                Text("%.2f кВт·год".format(it.dailyEnergyKwh))
            }

            Spacer(Modifier.height(20.dp))

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom()
                ),
                modelProducer = chartModelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}