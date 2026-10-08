package com.xaarlox.task06_microgridformsapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.xaarlox.task06_microgridformsapp.R
import com.xaarlox.task06_microgridformsapp.domain.SourceType

/**
 * Energy source type selection (solar or wind) using radio buttons
 *
 * @param selected current source type
 * @param onSelect selection of a different type
 * @param modifier external modifier
 * @param enabled whether selection is enabled (disabled during loading)
 */
@Composable
fun SourceTypeSelector(
    selected: SourceType,
    onSelect: (SourceType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.source_type_title),
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
        ) {
            SourceType.entries.forEach { type ->
                val label = when (type) {
                    SourceType.SOLAR -> R.string.source_solar
                    SourceType.WIND -> R.string.source_wind
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = type == selected,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = { onSelect(type) })
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = type == selected, onClick = null, enabled = enabled)
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(label))
                }
            }
        }
    }
}