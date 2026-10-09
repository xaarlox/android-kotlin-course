package com.xaarlox.task08_microgrid_room.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.viewmodel.SyncUiState

/**
 * Server sync card: displays the synchronization state (loading, success, error)
 * and provides a force-refresh button
 *
 * @param state Current synchronization state
 * @param onSync Callback invoked when "Sync with server" is clicked
 * @param modifier External modifier to be applied to the card
 */
@Composable
fun SyncBanner(
    state: SyncUiState,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        when (state) {
            SyncUiState.Idle -> Unit
            SyncUiState.Syncing -> LoadingRow(text = stringResource(R.string.sync_syncing))
            SyncUiState.UpToDate -> Text(stringResource(R.string.sync_up_to_date))
            is SyncUiState.Synced -> Text(stringResource(R.string.sync_synced, state.added))
            SyncUiState.Failed -> Text(
                text = stringResource(R.string.sync_failed),
                color = MaterialTheme.colorScheme.error
            )
        }
        AppButton(
            text = stringResource(R.string.sync_button),
            onClick = onSync,
            enabled = state != SyncUiState.Syncing
        )
    }
}