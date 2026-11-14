package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.StudyField
import com.pararam2006.tseletrack.domain.student.University
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UniversitiesUiState(
    val isLoading: Boolean = true,
    val universities: List<University> = emptyList(),
    val searchQuery: String = "",
    val selectedField: StudyField = StudyField.ComputerScience,
    val errorMessage: String? = null,
) {
    val visibleUniversities: List<University>
        get() = universities.filter { university ->
            searchQuery.isBlank() || university.name.contains(searchQuery, ignoreCase = true)
        }
}

class UniversitiesViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UniversitiesUiState())
    val uiState: StateFlow<UniversitiesUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectStudyField(field: StudyField) {
        _uiState.update { it.copy(selectedField = field) }
    }

    fun chooseUniversity(universityId: String) {
        viewModelScope.launch {
            try {
                repository.chooseUniversity(universityId)
                loadDashboard()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }

    fun retry() = loadDashboard()

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dashboard = repository.loadDashboard()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        universities = dashboard.universities,
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
