package com.xaarlox.task07_microgrid_mvvm.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xaarlox.task07_microgrid_mvvm.R
import com.xaarlox.task07_microgrid_mvvm.domain.PowerStatus
import com.xaarlox.task07_microgrid_mvvm.ui.color
import com.xaarlox.task07_microgrid_mvvm.ui.labelRes

/**
 * Loading state widget: a small indicator and text
 *
 * @param text label next to the indicator
 * @param modifier external modifier
 */
@Composable
fun LoadingRow(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(12.dp))
        Text(text = text)
    }
}

/**
 * Power status widget: "Status: ..." with a color depending on the level
 *
 * @param status power level
 * @param modifier external modifier
 */
@Composable
fun StatusText(status: PowerStatus, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.status_label, stringResource(status.labelRes())),
        color = status.color(),
        modifier = modifier
    )
}