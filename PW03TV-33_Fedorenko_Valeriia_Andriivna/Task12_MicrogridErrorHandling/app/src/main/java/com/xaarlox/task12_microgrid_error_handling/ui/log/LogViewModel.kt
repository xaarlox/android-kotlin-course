package com.xaarlox.task12_microgrid_error_handling.ui.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogEntry
import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogRepository
import com.xaarlox.task12_microgrid_error_handling.data.network.ApiResult
import com.xaarlox.task12_microgrid_error_handling.ui.UiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LogUiState(
    val entries: List<ErrorLogEntry> = emptyList(),
    val isUploading: Boolean = false
)

class LogViewModel(private val repository: ErrorLogRepository) : ViewModel() {

    private val uploading = MutableStateFlow(false)
    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val state: StateFlow<LogUiState> =
        combine(repository.entries, uploading) { entries, isUploading ->
            LogUiState(entries.take(MAX_SHOWN), isUploading)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LogUiState())

    fun upload() {
        if (uploading.value) return
        viewModelScope.launch {
            uploading.value = true
            val text = when (val result = repository.upload()) {
                is ApiResult.Success ->
                    if (result.data == 0) "Журнал порожній" else "Надіслано на сервер записів: ${result.data}"

                is ApiResult.Failure -> "Не вдалося надіслати: ${result.error.message}"
            }
            uploading.value = false
            _messages.send(UiMessage(text))
        }
    }

    fun clear() {
        viewModelScope.launch { repository.clear() }
    }

    private companion object {
        const val MAX_SHOWN = 200
    }
}