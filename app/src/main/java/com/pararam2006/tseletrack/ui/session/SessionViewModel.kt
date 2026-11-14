package com.pararam2006.tseletrack.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val isChecking: Boolean = true,
    val role: Role? = null,
)

class SessionViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val role = runCatching { authRepository.restoreSession() }.getOrNull()
            _uiState.value = SessionUiState(isChecking = false, role = role)
        }
    }

    fun onAuthenticated(role: Role) {
        _uiState.update { it.copy(role = role) }
    }

    fun onLoggedOut() {
        _uiState.value = SessionUiState(isChecking = false)
    }
}
