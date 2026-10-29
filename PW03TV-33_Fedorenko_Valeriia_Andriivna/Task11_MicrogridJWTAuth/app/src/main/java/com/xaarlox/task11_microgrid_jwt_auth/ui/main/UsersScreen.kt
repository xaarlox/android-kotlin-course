package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xaarlox.task11_microgrid_jwt_auth.domain.AppUser
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.ScreenHeader
import com.xaarlox.task11_microgrid_jwt_auth.ui.components.UserCard

/** Admin-only screen: data comes from the protected /api/admin/users endpoint */
@Composable
fun UsersScreen(users: List<AppUser>, onLoad: () -> Unit) {
    LaunchedEffect(Unit) { onLoad() }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { ScreenHeader("Користувачі", "Доступно лише адміністратору") }
        items(users, key = { it.username }) { UserCard(it) }
    }
}