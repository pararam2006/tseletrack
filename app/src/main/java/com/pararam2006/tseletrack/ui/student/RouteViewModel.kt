package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteUiState(
    val isLoading: Boolean = true,
    val contractStatus: StudentDocumentStatus = StudentDocumentStatus.Unavailable,
    val hasSelectedOffer: Boolean = false,
    val admissionState: AdmissionState? = null,
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

    fun retry() = loadDashboard()

    fun refresh() = loadDashboard()

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }
            try {
                loadJourneySnapshot()
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }

    private suspend fun loadJourneySnapshot() {
        val dashboard = repository.loadDashboard()
        val selection = repository.loadTargetOfferSelection()
        val admissionStatus = repository.loadAdmissionStatus()
        val contractStatus = repository.loadDocuments()
            .firstOrNull { it.type == StudentDocumentType.TargetContract }
            ?.status
            ?: StudentDocumentStatus.Unavailable
        updateDashboard(
            dashboard = dashboard,
            hasSelectedOffer = selection != null,
            admissionState = admissionStatus?.state,
            contractStatus = contractStatus,
        )
    }

    private fun updateDashboard(
        dashboard: StudentDashboard,
        hasSelectedOffer: Boolean,
        admissionState: AdmissionState?,
        contractStatus: StudentDocumentStatus,
    ) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                contractStatus = contractStatus,
                hasSelectedOffer = hasSelectedOffer,
                admissionState = admissionState,
                progressPercent = dashboard.routeProgressPercent,
                stages = dashboard.routeStages,
                errorMessage = null,
            )
        }
    }
}
