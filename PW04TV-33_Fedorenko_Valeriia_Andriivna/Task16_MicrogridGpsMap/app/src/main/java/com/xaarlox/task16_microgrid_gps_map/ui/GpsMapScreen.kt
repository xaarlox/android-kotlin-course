package com.xaarlox.task16_microgrid_gps_map.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xaarlox.task16_microgrid_gps_map.BuildConfig
import com.xaarlox.task16_microgrid_gps_map.domain.LocationState
import com.xaarlox.task16_microgrid_gps_map.ui.components.EnergyMap
import com.xaarlox.task16_microgrid_gps_map.ui.components.EnergyObjectCard
import com.xaarlox.task16_microgrid_gps_map.ui.components.FilterPanel
import com.xaarlox.task16_microgrid_gps_map.ui.components.LocationStatusChip
import com.xaarlox.task16_microgrid_gps_map.ui.components.MissingKeyCard
import com.xaarlox.task16_microgrid_gps_map.ui.components.PermissionBanner
import com.xaarlox.task16_microgrid_gps_map.ui.components.SearchField
import com.xaarlox.task16_microgrid_gps_map.ui.components.TypeLegend

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/** Either of the two permissions is enough: precise (GPS) or approximate (network) */
private fun Context.hasLocationPermission(): Boolean = LOCATION_PERMISSIONS.any {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun GpsMapScreen(viewModel: GpsMapViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var askedBefore by rememberSaveable { mutableStateOf(false) }
    var granted by remember { mutableStateOf(context.hasLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        askedBefore = true
        granted = context.hasLocationPermission()
    }
    LaunchedEffect(Unit) {
        if (!granted && !askedBefore) permissionLauncher.launch(LOCATION_PERMISSIONS)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { granted = context.hasLocationPermission() }
    LaunchedEffect(granted) { viewModel.onPermissionChanged(granted) }

    // After a denial with "Don't ask again" the system dialog is no longer shown
    val permanentlyDenied = !granted && askedBefore &&
        context.findActivity()?.let {
            !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.ACCESS_FINE_LOCATION)
        } == true

    LaunchedEffect(state.selectedId) {
        val index = state.visible.indexOfFirst { it.id == state.selectedId }
        if (index >= 0) {
            val listIndex = index + 1 // item zero of the list — the search and filters block
            val visible = listState.layoutInfo.visibleItemsInfo.any { it.index == listIndex }
            if (!visible) listState.animateScrollToItem(listIndex)
        }
    }

    var filtersExpanded by rememberSaveable { mutableStateOf(false) }
    val criteria = state.criteria
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val mapBlock: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                if (BuildConfig.HAS_MAPS_KEY) {
                    EnergyMap(
                        objects = state.visible,
                        selectedId = state.selectedId,
                        userLocation = state.userPoint,
                        distancesKm = state.distancesKm,
                        onMarkerClick = viewModel::onMarkerClick,
                        onMapClick = viewModel::onMapClick,
                        modifier = Modifier.fillMaxSize(),
                        overlay = {
                            LocationStatusChip(
                                state.location,
                                Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            )
                            if (state.location == LocationState.NoPermission) {
                                PermissionBanner(
                                    permanentlyDenied = permanentlyDenied,
                                    onRequestPermission = { permissionLauncher.launch(LOCATION_PERMISSIONS) },
                                    onOpenSettings = {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", context.packageName, null)
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(8.dp)
                                )
                            }
                        }
                    )
                } else {
                    MissingKeyCard()
                }
            }
            Spacer(Modifier.height(8.dp))
            TypeLegend()
        }
    }

    val listBlock: @Composable (Modifier) -> Unit = { modifier ->
        LazyColumn(
            modifier = modifier,
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
        ) {
            // Item zero: search, counter and the (collapsible) filter panel
            item(key = "controls") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            Text(if (count > 0) "  Фільтри ($count)" else "  Фільтри і сортування")
                        }
                    }
                    AnimatedVisibility(visible = filtersExpanded) {
                        FilterPanel(
                            criteria = criteria,
                            onToggleType = viewModel::toggleType,
                            onToggleStatus = viewModel::toggleStatus,
                            onTogglePowerClass = viewModel::togglePowerClass,
                            onSortClick = viewModel::onSortClick,
                            distanceSortEnabled = state.userPoint != null
                        )
                    }
                }
            }
            if (state.visible.isEmpty()) {
                item(key = "empty") {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Нічого не знайдено за вибраними фільтрами",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = viewModel::resetFilters) { Text("Скинути фільтри") }
                    }
                }
            }
            items(state.visible, key = { it.id }) { item ->
                EnergyObjectCard(
                    item = item,
                    selected = item.id == state.selectedId,
                    distanceKm = state.distancesKm[item.id],
                    onClick = { viewModel.onCardClick(item.id) },
                    modifier = Modifier.animateItem()
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
            "Мікрогрід на мапі",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Просунутий рівень: GPS, мапа, список, фільтри",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))

        if (landscape) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                mapBlock(Modifier.weight(1f).fillMaxSize())
                listBlock(Modifier.weight(1f).fillMaxSize())
            }
        } else {
            mapBlock(Modifier.fillMaxWidth().weight(0.46f))
            Spacer(Modifier.height(10.dp))
            listBlock(Modifier.fillMaxWidth().weight(0.54f))
        }
    }
}
