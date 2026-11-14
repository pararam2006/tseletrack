package com.pararam2006.tseletrack.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import com.pararam2006.tseletrack.data.network.userMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _authenticatedRoles = Channel<Role>(Channel.BUFFERED)
    val authenticatedRoles = _authenticatedRoles.receiveAsFlow()

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val state = _uiState.value
            if (state.email.isBlank() || state.password.isBlank()) {
                _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = "Введите email и пароль.")
                }
                return@launch
            }
            try {
                val role = authRepository.authenticate(state.email, state.password)
                _authenticatedRoles.send(role)
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
