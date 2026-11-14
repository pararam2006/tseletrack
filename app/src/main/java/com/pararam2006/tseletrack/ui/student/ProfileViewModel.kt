package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.StudentProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: StudentProfile = StudentProfile("", "", "", 0),
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun openSetting(setting: ProfileSetting) {
        // Stub: navigation or a settings dialog will be added with the backend flow.
        when (setting) {
            ProfileSetting.Theme,
            ProfileSetting.Notifications,
            ProfileSetting.Documents,
            -> Unit
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun retry() = loadDashboard()

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dashboard = repository.loadDashboard()
                _uiState.update { it.copy(isLoading = false, profile = dashboard.profile) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }
}

enum class ProfileSetting {
    Theme,
    Notifications,
    Documents,
}
