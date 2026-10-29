package com.xaarlox.task11_microgrid_jwt_auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Buttons to demonstrate server responses: 401 without a token, 401 with an invalid token, 403 without the required role */
@Composable
fun ProbeCard(
    result: String?,
    onAnonymous: () -> Unit,
    onBrokenToken: () -> Unit,
    onAdminResource: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Перевірка захисту", style = MaterialTheme.typography.titleSmall)
            Text(
                "Надішліть запит і подивіться, як відповідає сервер.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ProbeButton("Запит без токена", onAnonymous)
            ProbeButton("Запит із пошкодженим токеном", onBrokenToken)
            ProbeButton("Запит до адмін-ресурсу", onAdminResource)
            result?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun ProbeButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}