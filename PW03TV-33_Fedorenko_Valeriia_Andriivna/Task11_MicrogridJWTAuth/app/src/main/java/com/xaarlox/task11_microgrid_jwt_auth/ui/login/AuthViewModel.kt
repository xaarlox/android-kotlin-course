package com.xaarlox.task11_microgrid_jwt_auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xaarlox.task11_microgrid_jwt_auth.data.AuthRepository
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.toUserMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _login = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _login.asStateFlow()

    val session = sessionManager.session
    val notices = sessionManager.notices

    init {
        viewModelScope.launch {
            sessionManager.session.collectLatest { s ->
                if (s != null) {
                    delay((s.expiresAtMs - System.currentTimeMillis()).coerceAtLeast(0).milliseconds)
                    sessionManager.end("Термін дії токена минув. Увійдіть знову")
                }
            }
        }
    }

    fun onUsernameChange(text: String) = _login.update { it.copy(username = text, error = null) }
    fun onPasswordChange(text: String) = _login.update { it.copy(password = text, error = null) }

    fun fillDemo(username: String, password: String) =
        _login.update { it.copy(username = username, password = password, error = null) }

    fun login() {
        val form = _login.value
        if (form.isLoading) return
        if (form.username.isBlank() || form.password.isEmpty()) {
            _login.update { it.copy(error = "Введіть логін і пароль") }
            return
        }
        viewModelScope.launch {
            _login.update { it.copy(isLoading = true, error = null) }
            repository.login(form.username, form.password)
                .onSuccess { _login.value = LoginState() }
                .onFailure { e ->
                    _login.update {
                        it.copy(
                            isLoading = false,
                            error = e.toUserMessage()
                        )
                    }
                }
        }
    }

    fun logout() = sessionManager.end()
}