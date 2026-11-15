package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class DocumentsUiState(
    val isLoading: Boolean = true,
    val isUploading: Boolean = false,
    val documents: List<StudentDocument> = emptyList(),
    val errorMessage: String? = null,
    val actionMessage: String? = null,
    val isActionError: Boolean = false,
)

class DocumentsViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DocumentsUiState())
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()
    private val _documentsToOpen = Channel<com.pararam2006.tseletrack.domain.student.DownloadedDocument>(
        Channel.BUFFERED,
    )
    val documentsToOpen = _documentsToOpen.receiveAsFlow()

    init {
        loadDocuments()
    }

    fun uploadContract(document: StudentDocumentUpload) {
        validateContractUpload(document)?.let { message ->
            _uiState.update { it.copy(actionMessage = message, isActionError = true) }
            return
        }
        if (_uiState.value.isUploading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isUploading = true, actionMessage = null, isActionError = false)
            }
            try {
                repository.uploadContract(document)
                val documents = repository.loadDocuments()
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        documents = documents,
                        actionMessage = "Договор отправлен в ТулГУ на проверку",
                        isActionError = false,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        actionMessage = error.userMessage(),
                        isActionError = true,
                    )
                }
            }
        }
    }

    fun retry() = loadDocuments()

    fun refresh() {
        if (!_uiState.value.isLoading && !_uiState.value.isUploading) {
            loadDocuments()
        }
    }

    fun openDocument(documentId: String) {
        val document = _uiState.value.documents.firstOrNull { it.id == documentId } ?: return
        if (document.fileName == null || document.downloadUrl == null) return
        viewModelScope.launch {
            try {
                _documentsToOpen.send(repository.downloadDocument(document))
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(actionMessage = error.userMessage(), isActionError = true)
                }
            }
        }
    }

    fun documentOpenFailed() {
        _uiState.update {
            it.copy(
                actionMessage = "Не найдено приложение для открытия PDF",
                isActionError = true,
            )
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    documents = emptyList(),
                    errorMessage = null,
                    actionMessage = null,
                    isActionError = false,
                )
            }
            try {
                _uiState.update {
                    it.copy(isLoading = false, documents = repository.loadDocuments())
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }
}

internal fun validateContractUpload(document: StudentDocumentUpload): String? = when {
    document.mimeType != null && document.mimeType !in SUPPORTED_PDF_MIME_TYPES ->
        "Выберите договор в формате PDF"

    !document.fileName.endsWith(".pdf", ignoreCase = true) ->
        "Выберите договор в формате PDF"

    document.sizeBytes != null && document.sizeBytes > MAX_DOCUMENT_SIZE_BYTES ->
        "Размер файла не должен превышать 10 МБ"

    else -> null
}

private const val PDF_MIME_TYPE = "application/pdf"
private val SUPPORTED_PDF_MIME_TYPES = setOf(PDF_MIME_TYPE, "application/octet-stream")
private const val MAX_DOCUMENT_SIZE_BYTES = 10L * 1024 * 1024
