package com.pararam2006.tseletrack.ui.university

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.university.UniversityRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.role.OfferDemandStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class OfferDemandFilter {
    All,
    Underfilled,
    Oversubscribed,
}

data class UniversityUiState(
    val dashboard: UniversityDashboard? = null,
    val searchQuery: String = "",
    val selectedStage: String = "Все",
    val selectedOfferDemandFilter: OfferDemandFilter = OfferDemandFilter.All,
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

    val visibleTargetOfferMetrics
        get() = dashboard?.targetOfferMetrics.orEmpty()
            .filter { metric ->
                when (selectedOfferDemandFilter) {
                    OfferDemandFilter.All -> true
                    OfferDemandFilter.Underfilled ->
                        metric.demandStatus == OfferDemandStatus.Underfilled

                    OfferDemandFilter.Oversubscribed ->
                        metric.demandStatus == OfferDemandStatus.Oversubscribed
                }
            }
            .sortedWith(
                compareBy<com.pararam2006.tseletrack.domain.role.TargetOfferMetric> {
                    when (it.demandStatus) {
                        OfferDemandStatus.Underfilled -> 0
                        OfferDemandStatus.Oversubscribed -> 1
                        OfferDemandStatus.Filled -> 2
                    }
                }.thenBy { it.fillPercent },
            )
}

class UniversityViewModel(
    private val repository: UniversityRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UniversityUiState())
    val uiState: StateFlow<UniversityUiState> = _uiState.asStateFlow()
    private val _documentsToOpen = Channel<com.pararam2006.tseletrack.domain.student.DownloadedDocument>(
        Channel.BUFFERED,
    )
    val documentsToOpen = _documentsToOpen.receiveAsFlow()

    init {
        loadDashboard()
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun selectStage(value: String) {
        _uiState.update { it.copy(selectedStage = value) }
    }

    fun selectOfferDemandFilter(filter: OfferDemandFilter) {
        _uiState.update { it.copy(selectedOfferDemandFilter = filter) }
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

    fun openContractDocument(contractId: String) {
        val contract = _uiState.value.dashboard?.queue?.firstOrNull { it.id == contractId } ?: return
        val documentId = contract.documentId ?: return
        val fileName = contract.fileName ?: return
        viewModelScope.launch {
            try {
                _documentsToOpen.send(repository.downloadDocument(documentId, fileName))
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun documentOpenFailed() {
        _uiState.update { it.copy(errorMessage = "Не найдено приложение для открытия PDF") }
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
