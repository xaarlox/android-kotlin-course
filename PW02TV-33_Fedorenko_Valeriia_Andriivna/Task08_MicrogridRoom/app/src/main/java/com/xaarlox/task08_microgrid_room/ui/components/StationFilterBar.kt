package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.PowerFilter
import com.xaarlox.task08_microgrid_room.domain.SortOrder
import com.xaarlox.task08_microgrid_room.ui.labelRes

/**
 * List control panel: station name search, power level filter chips,
 * and a sort dropdown menu. Each control is a separate component backed
 * by its respective ViewModel state
 *
 * @param query Search query text
 * @param onQueryChange Callback invoked when the search query changes
 * @param filter Current filter selection
 * @param onFilterChange Callback invoked when a filter option is selected
 * @param sort Current sort option
 * @param onSortChange Callback invoked when a sort option is selected
 * @param modifier External modifier to be applied to the panel
 */
@Composable
fun StationFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    filter: PowerFilter,
    onFilterChange: (PowerFilter) -> Unit,
    sort: SortOrder,
    onSortChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        SearchField(query = query, onQueryChange = onQueryChange)
        PowerFilterChips(selected = filter, onSelect = onFilterChange)
        SortMenu(selected = sort, onSelect = onSortChange)
    }
}

/** Station name search field */
@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    AppTextField(
        label = stringResource(R.string.search_label),
        value = query,
        onValueChange = onQueryChange
    )
}

/** Row of filter chips for average power level (horizontally scrollable on narrow screens) */
@Composable
private fun PowerFilterChips(selected: PowerFilter, onSelect: (PowerFilter) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PowerFilter.entries.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(stringResource(option.labelRes())) }
            )
        }
    }
}

/** Button with a dropdown menu for selecting the sort order */
@Composable
private fun SortMenu(selected: SortOrder, onSelect: (SortOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(stringResource(R.string.sort_label, stringResource(selected.labelRes())))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SortOrder.entries.forEach { order ->
                    DropdownMenuItem(
                        text = { Text(stringResource(order.labelRes())) },
                        onClick = {
                            onSelect(order)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}