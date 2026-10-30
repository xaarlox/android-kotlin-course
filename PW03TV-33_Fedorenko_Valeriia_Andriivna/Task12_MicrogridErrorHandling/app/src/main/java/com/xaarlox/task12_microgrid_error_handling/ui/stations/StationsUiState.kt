package com.xaarlox.task12_microgrid_error_handling.ui.stations

import com.xaarlox.task12_microgrid_error_handling.data.network.AppError
import com.xaarlox.task12_microgrid_error_handling.data.network.RetryInfo
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultScenario
import com.xaarlox.task12_microgrid_error_handling.domain.Station

class FailureInfo(val error: AppError, val attempts: Int)

data class StationsUiState(
    val stations: List<Station> = emptyList(),
    val lastUpdatedMs: Long? = null,
    val isOnline: Boolean = true,
    val scenario: FaultScenario = FaultScenario.NORMAL,
    val isLoading: Boolean = false,
    val retry: RetryInfo? = null,
    val failure: FailureInfo? = null,
    val dialog: AppError? = null,
    val busyStationId: Int? = null
)