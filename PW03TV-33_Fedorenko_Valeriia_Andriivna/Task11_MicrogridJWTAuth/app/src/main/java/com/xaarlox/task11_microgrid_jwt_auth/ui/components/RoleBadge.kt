package com.xaarlox.task11_microgrid_jwt_auth.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task11_microgrid_jwt_auth.domain.Role

@Composable
fun RoleBadge(role: Role, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val admin = role == Role.ADMIN
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (admin) scheme.primary else scheme.surfaceVariant,
        contentColor = if (admin) scheme.onPrimary else scheme.onSurfaceVariant
    ) {
        Text(
            role.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}