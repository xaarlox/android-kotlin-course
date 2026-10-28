package com.xaarlox.task09_microgrid_repository.ui.advanced

import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import com.xaarlox.task09_microgrid_repository.domain.PowerStats

/** Single immutable screen state (Unidirectional Data Flow: the UI only renders this object) */
data class UiState(
    val records: List<PowerRecord> = emptyList(),
    val stats: PowerStats = PowerStats(),
    val dayFilter: String = "",
    val sortAscending: Boolean = true,
    val isLoading: Boolean = false,
    val message: String? = null
)