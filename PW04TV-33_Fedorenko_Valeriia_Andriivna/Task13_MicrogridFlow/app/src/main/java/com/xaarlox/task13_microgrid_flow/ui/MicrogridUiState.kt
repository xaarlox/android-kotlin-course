package com.xaarlox.task13_microgrid_flow.ui

import com.xaarlox.task13_microgrid_flow.domain.ForecastAccuracy
import com.xaarlox.task13_microgrid_flow.domain.HistoryPoint
import com.xaarlox.task13_microgrid_flow.domain.MicrogridSnapshot
import com.xaarlox.task13_microgrid_flow.domain.SensorKind
import com.xaarlox.task13_microgrid_flow.domain.SourceStatus
import com.xaarlox.task13_microgrid_flow.domain.StreamEvent

/** The single screen state, assembled in the ViewModel from five flows */
data class MicrogridUiState(
    val snapshot: MicrogridSnapshot? = null,
    val forecastAccuracy: ForecastAccuracy? = null,
    val statuses: Map<SensorKind, SourceStatus> = emptyMap(),
    val history: List<HistoryPoint> = emptyList(),
    val events: List<StreamEvent> = emptyList(),
    val randomFaultsEnabled: Boolean = false
) {
    /** true until the first value has arrived from all sensors (combine waits for every flow) */
    val isWaiting: Boolean get() = snapshot == null
}