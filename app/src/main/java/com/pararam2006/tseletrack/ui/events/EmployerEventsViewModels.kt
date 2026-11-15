package com.pararam2006.tseletrack.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.events.EmployerEventsException
import com.pararam2006.tseletrack.data.events.EmployerEventsRepository
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.ManagedEmployerEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentEmployerEventsUiState(
    val organizationName: String = "",
    val events: List<EmployerEventSummary> = emptyList(),
    val isLoading: Boolean = true,
    val updatingEventId: String? = null,
    val message: String? = null,
    val errorMessage: String? = null,
)

class StudentEmployerEventsViewModel(
    private val repository: EmployerEventsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentEmployerEventsUiState())
    val uiState: StateFlow<StudentEmployerEventsUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var registrationJob: Job? = null

    fun load(organizationName: String, forceRefresh: Boolean = false) {
        val requestedOrganization = organizationName.trim()
        if (requestedOrganization.isEmpty()) {
            loadJob?.cancel()
            registrationJob?.cancel()
            _uiState.value = StudentEmployerEventsUiState(
                isLoading = false,
                errorMessage = "Работодатель не указан",
            )
            return
        }
        val currentState = _uiState.value
        if (
            !forceRefresh &&
            requestedOrganization == currentState.organizationName &&
            !currentState.isLoading &&
            currentState.errorMessage == null
        ) {
            return
        }

        val organizationChanged = requestedOrganization != currentState.organizationName
        loadJob?.cancel()
        if (organizationChanged) registrationJob?.cancel()
        _uiState.update {
            it.copy(
                organizationName = requestedOrganization,
                events = if (organizationChanged) emptyList() else it.events,
                isLoading = true,
                updatingEventId = if (organizationChanged) null else it.updatingEventId,
                message = null,
                errorMessage = null,
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val events = repository.loadForStudent(requestedOrganization)
                _uiState.update { state ->
                    if (state.organizationName == requestedOrganization) {
                        state.copy(events = events, isLoading = false)
                    } else {
                        state
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { state ->
                    if (state.organizationName == requestedOrganization) {
                        state.copy(isLoading = false, errorMessage = error.eventsUserMessage())
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun toggleRegistration(eventId: String) {
        val state = _uiState.value
        if (state.isLoading || state.updatingEventId != null) return
        val current = state.events.firstOrNull { it.event.id == eventId } ?: return
        val organizationName = state.organizationName
        _uiState.update {
            it.copy(updatingEventId = eventId, message = null, errorMessage = null)
        }
        registrationJob = viewModelScope.launch {
            try {
                val updated = repository.setRegistration(eventId, !current.isRegistered)
                _uiState.update { state ->
                    if (
                        state.organizationName == organizationName &&
                        state.events.any { it.event.id == eventId }
                    ) {
                        state.copy(
                            events = state.events.map {
                                if (it.event.id == eventId) updated else it
                            },
                            updatingEventId = null,
                            message = if (updated.isRegistered) {
                                "Вы записаны на мероприятие"
                            } else {
                                "Запись отменена"
                            },
                        )
                    } else {
                        state
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { state ->
                    if (state.organizationName == organizationName) {
                        state.copy(
                            updatingEventId = null,
                            errorMessage = error.eventsUserMessage(),
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun retry() = load(_uiState.value.organizationName, forceRefresh = true)
}

data class CompanyEventsUiState(
    val organizationName: String = "",
    val events: List<ManagedEmployerEvent> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    val totalRegistrations: Int
        get() = events.sumOf(ManagedEmployerEvent::registeredCount)
}

class CompanyEventsViewModel(
    private val repository: EmployerEventsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CompanyEventsUiState())
    val uiState: StateFlow<CompanyEventsUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun load(organizationName: String, forceRefresh: Boolean = false) {
        val requestedOrganization = organizationName.trim()
        if (requestedOrganization.isEmpty()) {
            loadJob?.cancel()
            _uiState.value = CompanyEventsUiState(
                isLoading = false,
                errorMessage = "Работодатель не указан",
            )
            return
        }
        val currentState = _uiState.value
        if (
            !forceRefresh &&
            requestedOrganization == currentState.organizationName &&
            !currentState.isLoading &&
            currentState.errorMessage == null
        ) {
            return
        }

        val organizationChanged = requestedOrganization != currentState.organizationName
        loadJob?.cancel()
        _uiState.update {
            it.copy(
                organizationName = requestedOrganization,
                events = if (organizationChanged) emptyList() else it.events,
                isLoading = true,
                errorMessage = null,
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val events = repository.loadForEmployer(requestedOrganization)
                _uiState.update { state ->
                    if (state.organizationName == requestedOrganization) {
                        state.copy(events = events, isLoading = false)
                    } else {
                        state
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { state ->
                    if (state.organizationName == requestedOrganization) {
                        state.copy(isLoading = false, errorMessage = error.eventsUserMessage())
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun retry() = load(_uiState.value.organizationName, forceRefresh = true)
}

private fun Throwable.eventsUserMessage(): String =
    if (this is EmployerEventsException) {
        message.orEmpty()
    } else {
        userMessage()
    }
