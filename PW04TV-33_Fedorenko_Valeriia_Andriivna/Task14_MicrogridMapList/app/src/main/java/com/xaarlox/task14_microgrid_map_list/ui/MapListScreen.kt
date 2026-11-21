package com.xaarlox.task14_microgrid_map_list.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xaarlox.task14_microgrid_map_list.ui.components.EnergyObjectCard
import com.xaarlox.task14_microgrid_map_list.ui.components.MicrogridMap
import com.xaarlox.task14_microgrid_map_list.ui.components.TypeLegend

/**
 * The "list ↔ map" screen. The selection state lives in the ViewModel; here we only:
 *  - show the LazyColumn and the map from the same [MapListUiState];
 *  - scroll the list to the right card when the selection changes
 */
@Composable
fun MapListScreen(viewModel: MapListViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Selection on the map → the list scrolls to the card (if it is not currently visible)
    LaunchedEffect(state.selectedId) {
        val index = state.objects.indexOfFirst { it.id == state.selectedId }
        if (index >= 0) {
            val visible = listState.layoutInfo.visibleItemsInfo.any { it.index == index }
            if (!visible) listState.animateScrollToItem(index)
        }
    }

    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val mapBlock: @Composable (Modifier) -> Unit = { modifier ->
        MapBlock(state, viewModel, modifier)
    }
    val listBlock: @Composable (Modifier) -> Unit = { modifier ->
        LazyColumn(
            modifier = modifier,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
        ) {
            if (state.objects.isEmpty()) {
                item {
                    Text(
                        "Об'єктів немає. Додайте дані до списку.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(state.objects, key = { it.id }) { item ->
                EnergyObjectCard(
                    item = item,
                    selected = item.id == state.selectedId,
                    onClick = { viewModel.onCardClick(item.id) }
                )
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            "Об'єкти мікрогріду",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Просунутий рівень: список ↔ мапа · об'єктів: ${state.objects.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        if (landscape) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                mapBlock(Modifier
                    .weight(1f)
                    .fillMaxSize())
                listBlock(Modifier
                    .weight(1f)
                    .fillMaxSize())
            }
        } else {
            mapBlock(Modifier
                .fillMaxWidth()
                .height(300.dp))
            Spacer(Modifier.height(10.dp))
            listBlock(Modifier
                .weight(1f)
                .fillMaxWidth())
        }
    }
}

/** The map with a legend and a hint / information about the selected object */
@Composable
private fun MapBlock(state: MapListUiState, viewModel: MapListViewModel, modifier: Modifier) {
    Column(modifier) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            MicrogridMap(
                objects = state.objects,
                selectedId = state.selectedId,
                onMarkerClick = viewModel::onMarkerClick,
                onMapClick = viewModel::onMapClick,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                val selected = state.selected
                Text(
                    text = if (selected != null) {
                        "${selected.name} · ${selected.powerText}"
                    } else {
                        "Торкніться маркера або картки"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (selected != null) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        TypeLegend()
    }
}