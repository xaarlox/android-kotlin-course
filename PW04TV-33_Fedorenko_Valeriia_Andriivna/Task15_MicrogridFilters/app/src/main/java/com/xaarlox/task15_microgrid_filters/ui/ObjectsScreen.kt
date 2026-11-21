package com.xaarlox.task15_microgrid_filters.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xaarlox.task15_microgrid_filters.ui.components.EnergyObjectCard
import com.xaarlox.task15_microgrid_filters.ui.components.FilterPanel
import com.xaarlox.task15_microgrid_filters.ui.components.SearchField
import com.xaarlox.task15_microgrid_filters.ui.components.SelectionBar

/**
 * The list screen: search → (collapsible) filter and sorting panel → selection bar → LazyColumn.
 * There is no selection logic here at all — only displaying [ObjectsUiState] and calling the ViewModel
 */
@Composable
fun ObjectsScreen(viewModel: ObjectsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val criteria = state.criteria
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Column {
            Text(
                "Об'єкти мікрогріду",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Просунутий рівень: фільтри, сортування, вибір",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SearchField(query = criteria.query, onQueryChange = viewModel::onQueryChange)

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Показано ${state.visible.size} з ${state.totalCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { filtersExpanded = !filtersExpanded }) {
                Icon(Icons.Filled.FilterList, contentDescription = null)
                val count = criteria.activeFilterCount
                Text(
                    if (count > 0) "  Фільтри ($count)" else "  Фільтри і сортування"
                )
            }
        }

        AnimatedVisibility(visible = filtersExpanded) {
            FilterPanel(
                criteria = criteria,
                onToggleType = viewModel::toggleType,
                onToggleStatus = viewModel::toggleStatus,
                onTogglePowerClass = viewModel::togglePowerClass,
                onSortClick = viewModel::onSortClick
            )
        }

        AnimatedVisibility(visible = state.selection.count > 0) {
            SelectionBar(
                summary = state.selection,
                onSelectAllVisible = viewModel::selectAllVisible,
                onClear = viewModel::clearSelection
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
        ) {
            if (state.visible.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        hasFilters = criteria.activeFilterCount > 0,
                        onReset = viewModel::resetFilters
                    )
                }
            }
            items(state.visible, key = { it.id }) { item ->
                EnergyObjectCard(
                    item = item,
                    selected = item.id in state.selectedIds,
                    onClick = { viewModel.toggleSelection(item.id) },
                    modifier = Modifier.animateItem()   // smooth movement when sorting
                )
            }
        }
    }
}

/** A message shown when nothing is found for the current filters */
@Composable
private fun EmptyState(hasFilters: Boolean, onReset: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            if (hasFilters) "Нічого не знайдено за вибраними фільтрами" else "Об'єктів немає",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hasFilters) {
            Button(onClick = onReset) { Text("Скинути фільтри") }
        }
    }
}