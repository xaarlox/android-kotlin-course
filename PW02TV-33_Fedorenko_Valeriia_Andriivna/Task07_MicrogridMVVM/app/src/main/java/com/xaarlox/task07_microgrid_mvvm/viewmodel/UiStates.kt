package com.xaarlox.task07_microgrid_mvvm.viewmodel

import com.xaarlox.task07_microgrid_mvvm.domain.PowerStatus

/**
 * State of a single source card. The UI only displays this state (unidirectional data flow)
 */
sealed interface PowerUiState {
    object Loading : PowerUiState

    data class Success(val powerKw: Int, val status: PowerStatus) : PowerUiState

    object Error : PowerUiState
}

/**
 * Derived (computed) state of the entire microgrid based on the states of all sources
 */
sealed interface SummaryState {
    object Pending : SummaryState

    object Unavailable : SummaryState

    data class Partial(val totalKw: Int) : SummaryState

    data class Ready(val totalKw: Int, val status: PowerStatus) : SummaryState
}