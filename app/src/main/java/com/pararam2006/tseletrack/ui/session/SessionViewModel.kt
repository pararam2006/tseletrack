package com.pararam2006.tseletrack.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SessionUiState(
    val isChecking: Boolean = true,
    val role: Role? = null,
    val isLoggingOut: Boolean = false,
)

class SessionViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    private val _logoutCompleted = Channel<Unit>(Channel.BUFFERED)
    val logoutCompleted = _logoutCompleted.receiveAsFlow()
    private var restoreJob: Job? = null
    private var sessionGeneration: Long = 0

    init {
        val generation = sessionGeneration
        restoreJob = viewModelScope.launch {
            val role = try {
                authRepository.restoreSession()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
            if (generation == sessionGeneration) {
                _uiState.value = SessionUiState(isChecking = false, role = role)
            }
        }
    }

    fun onAuthenticated(role: Role) {
        if (_uiState.value.isLoggingOut) return
        restoreJob?.cancel()
        sessionGeneration++
        _uiState.update { it.copy(isChecking = false, role = role) }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return
        restoreJob?.cancel()
        sessionGeneration++
        _uiState.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Local session state is still cleared to avoid leaving the UI authenticated.
            }
            _uiState.value = SessionUiState(isChecking = false)
            _logoutCompleted.send(Unit)
        }
    }
}
