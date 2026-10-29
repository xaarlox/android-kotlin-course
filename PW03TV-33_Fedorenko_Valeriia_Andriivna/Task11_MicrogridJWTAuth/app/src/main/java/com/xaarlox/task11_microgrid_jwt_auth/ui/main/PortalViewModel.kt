package com.xaarlox.task11_microgrid_jwt_auth.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task11_microgrid_jwt_auth.data.PortalRepository
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PortalViewModel(
    private val repository: PortalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(PortalUiState())
    val state: StateFlow<PortalUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.session.map { it?.username }.distinctUntilChanged().collect { username ->
                _state.value = PortalUiState()
                if (username != null) loadStations()
            }
        }
    }

    fun loadStations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.stations()
                .onSuccess { list -> _state.update { it.copy(stations = list, isLoading = false) } }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    fail(e)
                }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            repository.users()
                .onSuccess { list -> _state.update { it.copy(users = list) } }
                .onFailure(::fail)
        }
    }

    fun toggleStation(id: Int) {
        if (_state.value.busyStationId != null) return
        viewModelScope.launch {
            _state.update { it.copy(busyStationId = id) }
            repository.toggle(id)
                .onSuccess { updated ->
                    _state.update { s ->
                        s.copy(stations = s.stations.map { if (it.id == updated.id) updated else it })
                    }
                }
                .onFailure(::fail)
            _state.update { it.copy(busyStationId = null) }
        }
    }

    fun probeAnonymous() = probe { repository.probeAnonymous() }
    fun probeBrokenToken() = probe { repository.probeBrokenToken() }
    fun probeAdminResource() = probe { repository.probeAdminResource() }

    fun messageShown() = _state.update { it.copy(message = null) }

    private fun probe(call: suspend () -> Result<*>) {
        viewModelScope.launch {
            val text = call().describe()
            _state.update { it.copy(probeResult = text) }
        }
    }

    private fun Result<*>.describe(): String {
        val e = exceptionOrNull() ?: return "HTTP 200: доступ дозволено"
        return if (e is HttpException) "HTTP ${e.code()}: ${e.toUserMessage()}" else e.toUserMessage()
    }

    /** If the session has already been terminated (401), the user sees a message about it on the login screen, so we remain silent here */
    private fun fail(e: Throwable) {
        if (sessionManager.session.value == null) return
        _state.update { it.copy(message = e.toUserMessage()) }
    }
}