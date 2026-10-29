package com.xaarlox.task10_microgrid_rest_api.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xaarlox.task10_microgrid_rest_api.data.ForecastRepository
import com.xaarlox.task10_microgrid_rest_api.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The ViewModel only combines flows into a single UiState and triggers updates.
 * It knows nothing about Retrofit and Gson: these are isolated in the data layer
 */
class ForecastViewModel(private val repository: ForecastRepository) : ViewModel() {

    private data class Status(val isLoading: Boolean = false, val message: String? = null)

    private val query = MutableStateFlow(DEFAULT_CITY)
    private val status = MutableStateFlow(Status())

    val uiState: StateFlow<ForecastUiState> = combine(
        repository.forecast,
        repository.summary,
        repository.location,
        query,
        status
    ) { days, summary, location, q, st ->
        ForecastUiState(days, summary, location, q, st.isLoading, st.message)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ForecastUiState(query = DEFAULT_CITY)
    )

    init {
        search()
    }

    fun onQueryChange(text: String) {
        query.value = text
    }

    fun search() {
        val city = query.value.trim()
        if (city.isEmpty()) {
            showMessage("Введіть назву міста")
            return
        }
        if (status.value.isLoading) return
        viewModelScope.launch {
            status.update { it.copy(isLoading = true) }
            repository.refresh(city)
                .onSuccess { fromCache ->
                    showMessage(if (fromCache) "Дані взято з кешу" else "Прогноз оновлено")
                }
                .onFailure {
                    val offlineHint =
                        if (uiState.value.days.isNotEmpty()) ". Показано збережені дані" else ""
                    showMessage(it.toUserMessage() + offlineHint)
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

    private companion object {
        const val DEFAULT_CITY = "Київ"
    }
}

class ForecastViewModelFactory(private val repository: ForecastRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ForecastViewModel(repository) as T
}