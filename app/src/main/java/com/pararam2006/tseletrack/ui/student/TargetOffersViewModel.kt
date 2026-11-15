package com.pararam2006.tseletrack.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.data.network.userMessage
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.TargetOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TargetOffersMessage {
    data object CatalogUpdated : TargetOffersMessage
    data object OfferSavedToRoute : TargetOffersMessage
    data object InvalidApplicantCode : TargetOffersMessage
    data object ApplicantCodeSaved : TargetOffersMessage
    data object AdmissionStatusUpdated : TargetOffersMessage
    data class Error(val value: String) : TargetOffersMessage
}

data class TargetOffersUiState(
    val isLoading: Boolean = true,
    val isRefreshingCatalog: Boolean = false,
    val offers: List<TargetOffer> = emptyList(),
    val searchQuery: String = "",
    val selectedOffer: TargetOffer? = null,
    val isCatalogExpanded: Boolean = true,
    val selectingOfferId: String? = null,
    val catalogWarning: String? = null,
    val applicantCode: String = "",
    val admissionStatus: AdmissionStatus? = null,
    val isSavingCode: Boolean = false,
    val isRefreshingStatus: Boolean = false,
    val errorMessage: String? = null,
    val actionMessage: TargetOffersMessage? = null,
) {
    val visibleOffers: List<TargetOffer>
        get() {
            val query = searchQuery.trim()
            val unselectedOffers = offers.filterNot { it.id == selectedOffer?.id }
            if (query.isEmpty()) return unselectedOffers
            return unselectedOffers.filter { offer ->
                listOfNotNull(
                    offer.directionCode,
                    offer.directionName,
                    offer.profile,
                    offer.proposerName,
                    offer.workplaceAddress,
                ).any { it.contains(query, ignoreCase = true) }
            }
        }

    val canSaveApplicantCode: Boolean
        get() = applicantCode.length in 6..20 && !isSavingCode
}

class TargetOffersViewModel(
    private val repository: StudentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TargetOffersUiState())
    val uiState: StateFlow<TargetOffersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showCatalog() {
        _uiState.update { it.copy(isCatalogExpanded = true, actionMessage = null) }
    }

    fun updateApplicantCode(value: String) {
        val digits = value.filter { it.isDigit() }.take(MAX_APPLICANT_CODE_LENGTH)
        _uiState.update { it.copy(applicantCode = digits, actionMessage = null) }
    }

    fun refreshCatalog() {
        if (_uiState.value.isRefreshingCatalog) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshingCatalog = true, actionMessage = null)
            }
            try {
                val catalog = repository.loadTargetOffers(forceRefresh = true)
                _uiState.update {
                    it.copy(
                        isRefreshingCatalog = false,
                        offers = catalog.items,
                        selectedOffer = catalog.items.firstOrNull { offer ->
                            offer.id == it.selectedOffer?.id
                        } ?: it.selectedOffer,
                        catalogWarning = catalog.warning,
                        actionMessage = TargetOffersMessage.CatalogUpdated,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshingCatalog = false,
                        actionMessage = TargetOffersMessage.Error(error.userMessage()),
                    )
                }
            }
        }
    }

    fun saveOfferToRoute(offerId: String) {
        if (_uiState.value.selectingOfferId != null) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(selectingOfferId = offerId, actionMessage = null)
            }
            try {
                val selection = repository.chooseTargetOffer(offerId)
                _uiState.update {
                    it.copy(
                        selectingOfferId = null,
                        selectedOffer = selection.offer,
                        isCatalogExpanded = false,
                        searchQuery = "",
                        actionMessage = TargetOffersMessage.OfferSavedToRoute,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        selectingOfferId = null,
                        actionMessage = TargetOffersMessage.Error(error.userMessage()),
                    )
                }
            }
        }
    }

    fun saveApplicantCode() {
        val code = _uiState.value.applicantCode
        if (code.length !in MIN_APPLICANT_CODE_LENGTH..MAX_APPLICANT_CODE_LENGTH) {
            _uiState.update {
                it.copy(actionMessage = TargetOffersMessage.InvalidApplicantCode)
            }
            return
        }
        if (_uiState.value.isSavingCode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingCode = true, actionMessage = null) }
            try {
                val status = repository.saveApplicantCode(code)
                _uiState.update {
                    it.copy(
                        isSavingCode = false,
                        applicantCode = "",
                        admissionStatus = status,
                        actionMessage = TargetOffersMessage.ApplicantCodeSaved,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSavingCode = false,
                        actionMessage = TargetOffersMessage.Error(error.userMessage()),
                    )
                }
            }
        }
    }

    fun refreshAdmissionStatus() {
        if (_uiState.value.isRefreshingStatus) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshingStatus = true, actionMessage = null) }
            try {
                val status = repository.refreshAdmissionStatus()
                _uiState.update {
                    it.copy(
                        isRefreshingStatus = false,
                        admissionStatus = status,
                        actionMessage = TargetOffersMessage.AdmissionStatusUpdated,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshingStatus = false,
                        actionMessage = TargetOffersMessage.Error(error.userMessage()),
                    )
                }
            }
        }
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val catalog = repository.loadTargetOffers()
                val selection = runCatching { repository.loadTargetOfferSelection() }.getOrNull()
                val status = runCatching { repository.loadAdmissionStatus() }.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        offers = catalog.items,
                        catalogWarning = catalog.warning,
                        selectedOffer = selection?.offer,
                        isCatalogExpanded = selection == null,
                        admissionStatus = status,
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.userMessage())
                }
            }
        }
    }

    private companion object {
        const val MIN_APPLICANT_CODE_LENGTH = 6
        const val MAX_APPLICANT_CODE_LENGTH = 20
    }
}
