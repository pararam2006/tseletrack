package com.pararam2006.tseletrack.ui.university

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.university.UniversityRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UniversityUiState(
    val dashboard: UniversityDashboard? = null,
    val searchQuery: String = "",
    val selectedStage: String = "Все",
    val errorMessage: String? = null,
) {
    val visibleStudents
        get() = dashboard?.students.orEmpty().filter { student ->
            val matchesSearch = searchQuery.isBlank() ||
                student.name.contains(searchQuery, ignoreCase = true)
            val normalizedStage = if (selectedStage == "Работа") "Трудоустройство" else selectedStage
            val matchesStage = selectedStage == "Все" || student.stage == normalizedStage
            matchesSearch && matchesStage
        }
}

class UniversityViewModel(
    private val repository: UniversityRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UniversityUiState())
    val uiState: StateFlow<UniversityUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun selectStage(value: String) {
        _uiState.update { it.copy(selectedStage = value) }
    }

    fun confirmContract(id: String) {
        viewModelScope.launch {
            try {
                repository.confirmContract(id)
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun declineContract(id: String) {
        viewModelScope.launch {
            try {
                repository.declineContract(id)
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun openProfileRow(index: Int) = Unit

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun retry() = loadDashboard()

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            try {
                _uiState.update { it.copy(dashboard = repository.loadDashboard()) }
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }
}
