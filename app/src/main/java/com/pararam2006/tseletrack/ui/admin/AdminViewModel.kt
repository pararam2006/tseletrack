package com.pararam2006.tseletrack.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.admin.AdminRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.AdminDashboard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val dashboard: AdminDashboard? = null,
    val searchQuery: String = "",
    val isActionRunning: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleUsers
        get() = dashboard?.users.orEmpty().filter { user ->
            searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)
        }
}

class AdminViewModel(
    private val repository: AdminRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var loadRequestId: Long = 0

    init {
        loadDashboard()
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun addUser() {
        runAction(repository::addUser)
    }

    fun exportData() {
        runAction(repository::exportData)
    }

    fun openRoles() {
        runAction(repository::openRoles)
    }

    fun createBackup() {
        runAction(repository::createBackup)
    }

    fun resetData() {
        runAction(repository::resetData)
    }

    fun openProfileRow(index: Int) = Unit

    fun retry() {
        if (!_uiState.value.isActionRunning) loadDashboard()
    }

    private fun loadDashboard() {
        if (_uiState.value.isActionRunning) return
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

    private fun runAction(action: suspend () -> Unit) {
        if (_uiState.value.isActionRunning) return
        loadJob?.cancel()
        ++loadRequestId
        _uiState.update { it.copy(isActionRunning = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                action()
                _uiState.update { it.copy(isActionRunning = false) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isActionRunning = false, errorMessage = error.userMessage())
                }
            }
        }
    }
}
