package com.xaarlox.task09_microgrid_repository.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/** Record creation form. The local state of the fields lives here, not in the ViewModel */
@Composable
fun AddRecordForm(
    onAdd: (dayText: String, powerText: String, onSuccess: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var dayText by remember { mutableStateOf("") }
    var powerText by remember { mutableStateOf("") }

    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Новий запис", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dayText,
                    onValueChange = { if (it.all(Char::isDigit)) dayText = it },
                    label = { Text("День") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = powerText,
                    onValueChange = { if (it.matches(Regex("\\d*[.,]?\\d*"))) powerText = it },
                    label = { Text("кВт") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            Button(
                onClick = {
                    onAdd(dayText, powerText) {
                        dayText = ""
                        powerText = ""
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Додати") }
        }
    }
}