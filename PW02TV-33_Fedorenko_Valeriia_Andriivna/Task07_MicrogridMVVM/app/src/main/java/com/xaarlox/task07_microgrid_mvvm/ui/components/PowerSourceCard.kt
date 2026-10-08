package com.xaarlox.task07_microgrid_mvvm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xaarlox.task07_microgrid_mvvm.R
import com.xaarlox.task07_microgrid_mvvm.viewmodel.PowerUiState

/**
 * MVVM: View. Universal energy source card
 * Contains no logic: only renders the provided [state] and reports clicks via [onRefresh]
 * The same card is used for both solar and wind stations
 *
 * @param title card title
 * @param state current state (loading, success, error)
 * @param onRefresh data refresh request (hoisted to the ViewModel)
 * @param modifier external modifier
 */
@Composable
fun PowerSourceCard(
    title: String,
    state: PowerUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)

            when (state) {
                PowerUiState.Loading -> LoadingRow(text = stringResource(R.string.loading))

                is PowerUiState.Success -> {
                    Text(text = stringResource(R.string.current_power, state.powerKw))
                    StatusText(status = state.status)
                }

                PowerUiState.Error -> Text(
                    text = stringResource(R.string.load_error),
                    color = MaterialTheme.colorScheme.error
                )
            }

            AppButton(
                text = stringResource(
                    if (state == PowerUiState.Error) R.string.retry_button else R.string.refresh_button
                ),
                onClick = onRefresh,
                enabled = state != PowerUiState.Loading
            )
        }
    }
}