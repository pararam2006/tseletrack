package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.student.StudentTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<StudentTask> = emptyList(),
    val errorMessage: String? = null,
)

class TasksViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun toggleTask(taskId: String) {
        val task = _uiState.value.tasks.firstOrNull { it.id == taskId } ?: return
        val updatedValue = !task.isCompleted
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map {
                    if (it.id == taskId) it.copy(isCompleted = updatedValue) else it
                },
            )
        }
        viewModelScope.launch {
            try {
                repository.updateTask(taskId, updatedValue)
            } catch (error: Exception) {
                _uiState.update { state ->
                    state.copy(
                        tasks = state.tasks.map {
                            if (it.id == taskId) it.copy(isCompleted = !updatedValue) else it
                        },
                        errorMessage = error.userMessage(),
                    )
                }
            }
        }
    }

    fun addTask() {
        viewModelScope.launch {
            try {
                repository.addTask()
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
                _uiState.update { it.copy(isLoading = false, tasks = dashboard.tasks) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }
}
