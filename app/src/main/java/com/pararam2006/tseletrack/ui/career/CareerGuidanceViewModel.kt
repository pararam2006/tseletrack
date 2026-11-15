package com.pararam2006.tseletrack.ui.career

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.career.CareerGuidanceStore
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.domain.career.CareerGuidanceContent
import com.pararam2006.tseletrack.domain.career.CareerGuidanceEngine
import com.pararam2006.tseletrack.domain.career.CareerGuidanceResult
import com.pararam2006.tseletrack.domain.career.CareerGuidanceSnapshot
import com.pararam2006.tseletrack.domain.career.CareerMatchSnapshot
import com.pararam2006.tseletrack.domain.career.CareerQuestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CareerGuidanceUiState(
    val isLoading: Boolean = true,
    val questionIndex: Int = 0,
    val answers: Map<String, Set<String>> = emptyMap(),
    val result: CareerGuidanceResult? = null,
    val isSynced: Boolean = false,
    val isSyncing: Boolean = false,
    val syncError: String? = null,
) {
    val currentQuestion: CareerQuestion?
        get() = CareerGuidanceContent.questions.getOrNull(questionIndex)

    val selectedOptionIds: Set<String>
        get() = currentQuestion?.let { answers[it.id].orEmpty() }.orEmpty()

    val hasSavedProgress: Boolean
        get() = result == null && answers.isNotEmpty()

    val canContinue: Boolean
        get() = selectedOptionIds.isNotEmpty()
}

class CareerGuidanceViewModel(
    private val store: CareerGuidanceStore,
    private val engine: CareerGuidanceEngine,
    private val studentRepository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CareerGuidanceUiState())
    val uiState: StateFlow<CareerGuidanceUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var persistJob: Job? = null
    private var journeyGeneration: Long = 0

    init {
        val generation = journeyGeneration
        loadJob = viewModelScope.launch {
            val snapshot = store.load()
            if (generation == journeyGeneration) {
                _uiState.value = CareerGuidanceUiState(
                    isLoading = false,
                    questionIndex = snapshot.questionIndex.coerceIn(
                        0,
                        CareerGuidanceContent.questions.lastIndex,
                    ),
                    answers = snapshot.answers.mapValues { (_, value) -> value.toSet() },
                    result = engine.restore(snapshot.matches),
                    isSynced = snapshot.isSynced,
                )
            }
        }
    }

    fun startNew() {
        if (_uiState.value.isSyncing) return
        journeyGeneration++
        loadJob?.cancel()
        persistJob?.cancel()
        _uiState.value = CareerGuidanceUiState(isLoading = false)
        persistJob = viewModelScope.launch { store.clear() }
    }

    fun selectOption(optionId: String) {
        val question = _uiState.value.currentQuestion ?: return
        val selected = _uiState.value.selectedOptionIds
        val updatedSelection = when {
            question.maxSelections == 1 -> setOf(optionId)
            optionId in selected -> selected - optionId
            selected.size < question.maxSelections -> selected + optionId
            else -> selected
        }
        _uiState.update { state ->
            state.copy(
                answers = state.answers + (question.id to updatedSelection),
                syncError = null,
            )
        }
        persistProgress()
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.canContinue) return
        if (state.questionIndex < CareerGuidanceContent.questions.lastIndex) {
            _uiState.update { it.copy(questionIndex = it.questionIndex + 1) }
            persistProgress()
            return
        }

        val result = engine.calculate(state.answers)
        _uiState.update { it.copy(result = result, isSynced = false, syncError = null) }
        persistSnapshot()
    }

    fun previousQuestion(): Boolean {
        val currentIndex = _uiState.value.questionIndex
        if (currentIndex == 0) return false
        _uiState.update { it.copy(questionIndex = currentIndex - 1) }
        persistProgress()
        return true
    }

    fun syncResult() {
        val state = _uiState.value
        if (state.result == null || state.isSyncing) return
        val generation = journeyGeneration
        _uiState.update { it.copy(isSyncing = true, syncError = null) }
        viewModelScope.launch {
            try {
                studentRepository.completeCareerGuidance()
                if (generation == journeyGeneration) {
                    _uiState.update { it.copy(isSyncing = false, isSynced = true) }
                    persistJob?.cancel()
                    store.save(_uiState.value.toSnapshot())
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (generation == journeyGeneration) {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncError = error.userMessage(),
                        )
                    }
                }
            }
        }
    }

    fun clearSyncError() {
        _uiState.update { it.copy(syncError = null) }
    }

    private fun persistProgress() = persistSnapshot()

    private fun persistSnapshot() {
        val snapshot = _uiState.value.toSnapshot()
        persistJob?.cancel()
        persistJob = viewModelScope.launch { store.save(snapshot) }
    }

    private fun CareerGuidanceUiState.toSnapshot(): CareerGuidanceSnapshot {
        val matches = result?.matches.orEmpty().map { match ->
            CareerMatchSnapshot(match.direction.id, match.percent)
        }
        return CareerGuidanceSnapshot(
            questionIndex = questionIndex,
            answers = answers.mapValues { (_, value) -> value.toList().sorted() },
            matches = matches,
            isSynced = isSynced,
        )
    }
}
