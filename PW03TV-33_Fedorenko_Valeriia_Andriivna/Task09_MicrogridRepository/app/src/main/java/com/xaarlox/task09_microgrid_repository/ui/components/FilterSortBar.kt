package com.xaarlox.task09_microgrid_repository.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FilterSortBar(
    filter: String,
    sortAscending: Boolean,
    onFilterChange: (String) -> Unit,
    onToggleSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = filter,
            onValueChange = { if (it.all(Char::isDigit)) onFilterChange(it) },
            placeholder = { Text("Фільтр по дню") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onToggleSort) {
            Text(if (sortAscending) "День ↑" else "День ↓")
        }
    }
}