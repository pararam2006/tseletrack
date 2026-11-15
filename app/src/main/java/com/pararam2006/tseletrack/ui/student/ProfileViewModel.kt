package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: StudentProfile = StudentProfile("", "", "", 0),
    val isAcademicPerformanceAvailable: Boolean = false,
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var loadRequestId: Long = 0

    init {
        loadDashboard()
    }

    fun retry() = loadDashboard()

    fun refresh() {
        if (!_uiState.value.isLoading) loadDashboard()
    }

    private fun loadDashboard() {
        loadJob?.cancel()
        val requestId = ++loadRequestId
        _uiState.value = ProfileUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            try {
                val dashboard = repository.loadDashboard()
                val academicPerformanceAvailable =
                    dashboard.routeStages.hasAcademicPerformanceAccess()
                if (requestId == loadRequestId) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = dashboard.profile,
                            isAcademicPerformanceAvailable = academicPerformanceAvailable,
                        )
                    }
                }
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
}

internal fun List<RouteStage>.hasAcademicPerformanceAccess(): Boolean = any { stage ->
    stage.type == RouteStageType.Education && stage.status in setOf(
        RouteStageStatus.Current,
        RouteStageStatus.Completed,
    )
}
