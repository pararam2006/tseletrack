package com.pararam2006.tseletrack.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import com.pararam2006.tseletrack.data.network.userMessage
import kotlinx.coroutines.CancellationException
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
        val state = _uiState.value
        if (state.isSubmitting) return
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Введите email и пароль.")
            }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val role = authRepository.authenticate(state.email, state.password)
                _authenticatedRoles.send(role)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun startStudentDemo() {
        startDemo(authRepository::startStudentDemo)
    }

    fun startUniversityDemo() {
        startDemo(authRepository::startUniversityDemo)
    }

    private fun startDemo(resolveRole: suspend () -> Role) {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                _authenticatedRoles.send(resolveRole())
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
