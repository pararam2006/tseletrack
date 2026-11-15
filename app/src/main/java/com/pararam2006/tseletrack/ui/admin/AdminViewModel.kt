package com.pararam2006.tseletrack.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.admin.AdminRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.role.AdminDashboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val dashboard: AdminDashboard? = null,
    val searchQuery: String = "",
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

    private fun runAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            }
        }
    }
}
