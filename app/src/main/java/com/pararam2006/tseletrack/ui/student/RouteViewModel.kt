package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.RouteStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteUiState(
    val isLoading: Boolean = true,
    val progressPercent: Int = 0,
    val stages: List<RouteStage> = emptyList(),
    val errorMessage: String? = null,
)

class RouteViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun signContract() {
        viewModelScope.launch {
            try {
                repository.signContract()
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun retry() = loadDashboard()

    fun refresh() = loadDashboard()

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dashboard = repository.loadDashboard()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        progressPercent = dashboard.routeProgressPercent,
                        stages = dashboard.routeStages,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }
}
