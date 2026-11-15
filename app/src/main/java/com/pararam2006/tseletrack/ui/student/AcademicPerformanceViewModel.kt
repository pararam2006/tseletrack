package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.data.student.AcademicPerformanceRepository
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AcademicPerformanceUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val performance: AcademicPerformance? = null,
    val isEditingTracking: Boolean = false,
    val recordBookNumber: String = "",
    val groupTitle: String = "",
    val message: String? = null,
    val isMessageError: Boolean = false,
)

class AcademicPerformanceViewModel(
    private val repository: AcademicPerformanceRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AcademicPerformanceUiState())
    val uiState: StateFlow<AcademicPerformanceUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateRecordBookNumber(value: String) {
        _uiState.update {
            it.copy(
                recordBookNumber = value.take(MAX_INPUT_LENGTH),
                message = null,
            )
        }
    }

    fun updateGroupTitle(value: String) {
        _uiState.update {
            it.copy(
                groupTitle = value.take(MAX_INPUT_LENGTH),
                message = null,
            )
        }
    }

    fun connect() {
        val recordBookNumber = uiState.value.recordBookNumber.trim()
        val groupTitle = uiState.value.groupTitle.trim()
        val validationMessage = validateTrackingInput(recordBookNumber, groupTitle)
        if (validationMessage != null) {
            _uiState.update { it.copy(message = validationMessage, isMessageError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshing = true, message = null, isMessageError = false)
            }
            runCatching { repository.connect(recordBookNumber, groupTitle) }
                .onSuccess { performance ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            performance = performance,
                            isEditingTracking = false,
                            recordBookNumber = "",
                            groupTitle = "",
                            message = "Успеваемость подключена",
                            isMessageError = false,
                        )
                    }
                }
                .onFailure(::showActionError)
        }
    }

    fun editTracking() {
        val performance = uiState.value.performance ?: return
        _uiState.update {
            it.copy(
                isEditingTracking = true,
                recordBookNumber = "",
                groupTitle = performance.groupTitle,
                message = null,
                isMessageError = false,
            )
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                isEditingTracking = false,
                recordBookNumber = "",
                groupTitle = "",
                message = null,
                isMessageError = false,
            )
        }
    }

    fun refresh() {
        if (uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshing = true, message = null, isMessageError = false)
            }
            runCatching { repository.refresh() }
                .onSuccess { performance ->
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            performance = performance,
                            message = "Данные обновлены",
                            isMessageError = false,
                        )
                    }
                }
                .onFailure(::showActionError)
        }
    }

    fun retry() = load()

    /** Reloads account-bound data when the screen becomes active again. */
    fun reloadForActiveUser() {
        if (!uiState.value.isLoading && !uiState.value.isRefreshing) load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = AcademicPerformanceUiState(isLoading = true)
            runCatching { repository.load() }
                .onSuccess { performance ->
                    _uiState.update {
                        it.copy(isLoading = false, performance = performance)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = error.userMessage(),
                            isMessageError = true,
                        )
                    }
                }
        }
    }

    private fun showActionError(error: Throwable) {
        _uiState.update {
            it.copy(
                isRefreshing = false,
                message = error.userMessage(),
                isMessageError = true,
            )
        }
    }

    private companion object {
        const val MAX_INPUT_LENGTH = 32
    }
}

internal fun validateTrackingInput(
    recordBookNumber: String,
    groupTitle: String,
): String? = when {
    recordBookNumber.length !in 3..32 -> "Введите номер зачётной книжки"
    groupTitle.length !in 2..32 -> "Введите номер учебной группы"
    else -> null
}
