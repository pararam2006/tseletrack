package com.pararam2006.tseletrack.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.company.CompanyRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    val processingCandidateId: String? = null,
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
    private var loadJob: Job? = null
    private var loadRequestId: Long = 0

    init {
        loadDashboard()
    }

    fun selectFilter(filter: CandidateFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun advanceCandidate(id: String, status: CandidateStatus) {
        mutateCandidate(id) { repository.updateCandidateStatus(id, status) }
    }

    fun declineCandidate(id: String) {
        mutateCandidate(id) { repository.declineCandidate(id) }
    }

    fun openProfileRow(index: Int) = Unit

    fun retry() {
        if (_uiState.value.processingCandidateId == null) loadDashboard()
    }

    private fun loadDashboard() {
        if (_uiState.value.processingCandidateId != null) return
        loadJob?.cancel()
        val requestId = ++loadRequestId
        _uiState.update { it.copy(errorMessage = null) }
        loadJob = viewModelScope.launch {
            try {
                val dashboard = repository.loadDashboard()
                if (requestId == loadRequestId) {
                    _uiState.update { it.copy(dashboard = dashboard) }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (requestId == loadRequestId) {
                    _uiState.update { it.copy(errorMessage = error.userMessage()) }
                }
            }
        }
    }

    private fun mutateCandidate(
        candidateId: String,
        mutation: suspend () -> Unit,
    ) {
        val state = _uiState.value
        if (state.processingCandidateId != null) return
        if (state.dashboard?.candidates?.none { it.id == candidateId } != false) return

        loadJob?.cancel()
        ++loadRequestId
        _uiState.update {
            it.copy(processingCandidateId = candidateId, errorMessage = null)
        }
        viewModelScope.launch {
            try {
                mutation()
                val dashboard = repository.loadDashboard()
                _uiState.update {
                    if (it.processingCandidateId == candidateId) {
                        it.copy(dashboard = dashboard, processingCandidateId = null)
                    } else {
                        it
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    if (it.processingCandidateId == candidateId) {
                        it.copy(
                            processingCandidateId = null,
                            errorMessage = error.userMessage(),
                        )
                    } else {
                        it
                    }
                }
            }
        }
    }
}
