package com.xaarlox.task08_microgrid_room.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.GridStats
import com.xaarlox.task08_microgrid_room.domain.PowerFilter
import com.xaarlox.task08_microgrid_room.domain.SortOrder
import com.xaarlox.task08_microgrid_room.ui.components.AdaptiveLayout
import com.xaarlox.task08_microgrid_room.ui.components.GridStatsCard
import com.xaarlox.task08_microgrid_room.ui.components.LoadingRow
import com.xaarlox.task08_microgrid_room.ui.components.StationCard
import com.xaarlox.task08_microgrid_room.ui.components.StationFilterBar
import com.xaarlox.task08_microgrid_room.ui.components.StationForm
import com.xaarlox.task08_microgrid_room.ui.components.SyncBanner
import com.xaarlox.task08_microgrid_room.ui.theme.advancedScreenBackground
import com.xaarlox.task08_microgrid_room.viewmodel.StationListState
import com.xaarlox.task08_microgrid_room.viewmodel.StationViewModel
import com.xaarlox.task08_microgrid_room.viewmodel.SyncUiState

/**
 * Main screen composable. Observes StateFlow from the ViewModel (via collectAsState) and strictly renders UI state
 *
 * Adaptability: displays a single unified scrollable column on compact screens (form at the top, stations below),
 * and two independent side-by-side panes on expanded screens: input and summary on the left, station list on the right
 *
 * @param viewModel ViewModel providing the screen state
 */
@Composable
fun StationScreen(viewModel: StationViewModel) {
    val listState by viewModel.listState.collectAsState()
    val stats by viewModel.gridStats.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val sort by viewModel.sort.collectAsState()

    AdaptiveLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(advancedScreenBackground())
            .safeDrawingPadding(),
        compact = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                overviewItems(viewModel, stats, syncState)
                stationListItems(viewModel, listState, filter, sort)
            }
        },
        wide = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) { overviewItems(viewModel, stats, syncState) }

                LazyColumn(
                    modifier = Modifier.weight(1.3f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) { stationListItems(viewModel, listState, filter, sort) }
            }
        }
    )
}

/** "Summary + sync + input form" panel */
private fun LazyListScope.overviewItems(
    viewModel: StationViewModel,
    stats: GridStats,
    syncState: SyncUiState
) {
    item(key = "title") {
        Text(
            text = stringResource(R.string.screen_title),
            style = MaterialTheme.typography.headlineSmall
        )
    }
    item(key = "stats") { GridStatsCard(stats) }
    item(key = "sync") { SyncBanner(state = syncState, onSync = { viewModel.sync(force = true) }) }
    item(key = "form") {
        StationForm(
            form = viewModel.form,
            onNameChange = viewModel::onNameChange,
            onPowersChange = viewModel::onPowersChange,
            onVoltagesChange = viewModel::onVoltagesChange,
            onSubmit = viewModel::onAddStation
        )
    }
}

/** "Search / filter / sort + station list" panel */
private fun LazyListScope.stationListItems(
    viewModel: StationViewModel,
    listState: StationListState,
    filter: PowerFilter,
    sort: SortOrder
) {
    item(key = "filters") {
        StationFilterBar(
            query = viewModel.query,
            onQueryChange = viewModel::onQueryChange,
            filter = filter,
            onFilterChange = viewModel::onFilterChange,
            sort = sort,
            onSortChange = viewModel::onSortChange
        )
    }
    item(key = "list_title") {
        Text(
            text = stringResource(R.string.list_title, listState.items.size, listState.totalCount),
            style = MaterialTheme.typography.titleMedium
        )
    }
    when {
        listState.isLoading -> item(key = "loading") {
            LoadingRow(text = stringResource(R.string.loading))
        }

        listState.items.isEmpty() -> item(key = "empty") {
            Text(
                text = stringResource(
                    if (listState.totalCount == 0) R.string.list_empty else R.string.list_empty_filtered
                )
            )
        }

        else -> items(items = listState.items, key = { it.id }) { station ->
            StationCard(station = station, onDelete = { viewModel.deleteStation(station.id) })
        }
    }
}