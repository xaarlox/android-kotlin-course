package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task11_microgrid_jwt_auth.domain.Session
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.ProbeCard
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.ScreenHeader
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.SessionInfoCard
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SessionScreen(
    session: Session,
    probeResult: String?,
    onAnonymous: () -> Unit,
    onBrokenToken: () -> Unit,
    onAdminResource: () -> Unit,
    onLogout: () -> Unit
) {
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000.milliseconds)
            nowMs = System.currentTimeMillis()
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ScreenHeader("Сесія", "Дані з JWT-токена") }
        item { SessionInfoCard(session, nowMs) }
        item { ProbeCard(probeResult, onAnonymous, onBrokenToken, onAdminResource) }
        item {
            Button(
                onClick = onLogout,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Вийти") }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}