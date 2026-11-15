package com.pararam2006.tseletrack.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.company.CompanyRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CandidateFilter(val title: String) {
    All("Все"),
    Education("Обучение"),
    Employment("Трудоустройство"),
    Completed("Трудоустроены"),
}

data class CompanyUiState(
    val dashboard: CompanyDashboard? = null,
    val selectedFilter: CandidateFilter = CandidateFilter.All,
    val errorMessage: String? = null,
) {
    val visibleCandidates
        get() = dashboard?.candidates.orEmpty().filter { candidate ->
            when (selectedFilter) {
                CandidateFilter.All -> true
                CandidateFilter.Education -> candidate.status == CandidateStatus.Education
                CandidateFilter.Employment -> candidate.status == CandidateStatus.Employment
                CandidateFilter.Completed -> candidate.status == CandidateStatus.Completed
            }
        }
}

class CompanyViewModel(
    private val repository: CompanyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompanyUiState())
    val uiState: StateFlow<CompanyUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun selectFilter(filter: CandidateFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun advanceCandidate(id: String, status: CandidateStatus) {
        viewModelScope.launch {
            try {
                repository.updateCandidateStatus(id, status)
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun declineCandidate(id: String) {
        viewModelScope.launch {
            try {
                repository.declineCandidate(id)
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun openProfileRow(index: Int) = Unit

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
