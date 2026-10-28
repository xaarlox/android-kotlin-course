package com.xaarlox.task09_microgrid_repository.ui.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xaarlox.task09_microgrid_repository.data.MicrogridRepository
import com.xaarlox.task09_microgrid_repository.domain.PowerRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The ViewModel contains no business rules: only UI state and "data → presentation" transformations
 *
 * Filtering and sorting are display concerns, so the DB holds all records
 * while the UI presents the requested slice
 */
class AdvancedViewModel(private val repository: MicrogridRepository) : ViewModel() {

    private data class Status(val isLoading: Boolean = false, val message: String? = null)

    private val dayFilter = MutableStateFlow("")
    private val sortAscending = MutableStateFlow(true)
    private val status = MutableStateFlow(Status())

    /**
     * combine merges 5 flows into a single UiState. stateIn with WhileSubscribed(5000) pauses
     * collection when the screen is in the background for longer than 5s, while surviving screen rotations
     */
    val uiState: StateFlow<UiState> = combine(
        repository.records,
        repository.stats,
        dayFilter,
        sortAscending,
        status
    ) { records, stats, filter, ascending, st ->
        val wantedDay = filter.toIntOrNull()
        val filtered = if (wantedDay == null) records else records.filter { it.day == wantedDay }
        val sorted = if (ascending) {
            filtered.sortedWith(compareBy<PowerRecord> { it.day }.thenBy { it.id })
        } else {
            filtered.sortedWith(compareByDescending<PowerRecord> { it.day }.thenByDescending { it.id })
        }
        UiState(sorted, stats, filter, ascending, st.isLoading, st.message)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun onDayFilterChange(text: String) {
        dayFilter.value = text
    }

    fun toggleSort() {
        sortAscending.update { !it }
    }

    fun addRecord(dayText: String, powerText: String, onSuccess: () -> Unit) {
        val day = dayText.toIntOrNull()
        val power = powerText.replace(',', '.').toDoubleOrNull()
        if (day == null || power == null) {
            showMessage("Введіть день і потужність")
            return
        }
        viewModelScope.launch {
            repository.addRecord(day, power)
                .onSuccess { onSuccess() }
                .onFailure { showMessage(it.message ?: "Не вдалося додати запис") }
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }

    fun loadForecasts() {
        if (status.value.isLoading) return
        viewModelScope.launch {
            status.update { it.copy(isLoading = true) }
            repository.refreshForecasts()
                .onSuccess { fromCache ->
                    showMessage(if (fromCache) "Прогнози взято з кешу" else "Прогнози завантажено")
                }
                .onFailure {
                    showMessage("Помилка завантаження: ${it.message ?: "невідома помилка"}")
                }
            status.update { it.copy(isLoading = false) }
        }
    }

    fun messageShown() {
        status.update { it.copy(message = null) }
    }

    private fun showMessage(text: String) {
        status.update { it.copy(message = text) }
    }
}

class AdvancedViewModelFactory(private val repository: MicrogridRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AdvancedViewModel(repository) as T
}