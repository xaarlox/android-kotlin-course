package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import com.xaarlox.task11_microgrid_jwt_auth.domain.AppUser
import com.xaarlox.task11_microgrid_jwt_auth.domain.Station

data class PortalUiState(
    val stations: List<Station> = emptyList(),
    val users: List<AppUser> = emptyList(),
    val isLoading: Boolean = false,
    val busyStationId: Int? = null,
    val probeResult: String? = null,
    val message: String? = null
)