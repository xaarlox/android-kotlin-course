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
import com.xaarlox.task07_microgrid_mvvm.viewmodel.SummaryState

/**
 * MVVM: View. Microgrid overall state card
 * Displays the derived state calculated by the ViewModel from the states of all sources
 *
 * @param state overall microgrid state
 * @param modifier external modifier
 */
@Composable
fun SummaryCard(state: SummaryState, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.summary_title),
                style = MaterialTheme.typography.titleMedium
            )
            when (state) {
                SummaryState.Pending -> LoadingRow(text = stringResource(R.string.summary_pending))

                SummaryState.Unavailable -> Text(
                    text = stringResource(R.string.summary_unavailable),
                    color = MaterialTheme.colorScheme.error
                )

                is SummaryState.Partial -> Text(
                    text = stringResource(R.string.summary_partial, state.totalKw)
                )

                is SummaryState.Ready -> {
                    Text(text = stringResource(R.string.summary_total, state.totalKw))
                    StatusText(status = state.status)
                }
            }
        }
    }
}