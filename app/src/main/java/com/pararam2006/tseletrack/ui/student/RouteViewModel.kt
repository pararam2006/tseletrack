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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
    private var loadJob: Job? = null
    private var loadRequestId: Long = 0

    init {
        loadDashboard()
    }

    fun retry() = loadDashboard()

    fun refresh() = loadDashboard()

    private fun loadDashboard() {
        loadJob?.cancel()
        val requestId = ++loadRequestId
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val snapshot = loadJourneySnapshot()
                if (requestId == loadRequestId) updateDashboard(snapshot)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (requestId == loadRequestId) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.userMessage())
                    }
                }
            }
        }
    }

    private suspend fun loadJourneySnapshot(): JourneySnapshot {
        val dashboard = repository.loadDashboard()
        val selection = repository.loadTargetOfferSelection()
        val admissionStatus = repository.loadAdmissionStatus()
        val contractStatus = repository.loadDocuments()
            .firstOrNull { it.type == StudentDocumentType.TargetContract }
            ?.status
            ?: StudentDocumentStatus.Unavailable
        return JourneySnapshot(
            dashboard = dashboard,
            hasSelectedOffer = selection != null,
            admissionState = admissionStatus?.state,
            contractStatus = contractStatus,
        )
    }

    private fun updateDashboard(snapshot: JourneySnapshot) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                contractStatus = snapshot.contractStatus,
                hasSelectedOffer = snapshot.hasSelectedOffer,
                admissionState = snapshot.admissionState,
                progressPercent = snapshot.dashboard.routeProgressPercent,
                stages = snapshot.dashboard.routeStages,
                errorMessage = null,
            )
        }
    }

    private data class JourneySnapshot(
        val dashboard: StudentDashboard,
        val hasSelectedOffer: Boolean,
        val admissionState: AdmissionState?,
        val contractStatus: StudentDocumentStatus,
    )
}
