package com.pararam2006.tseletrack.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import com.pararam2006.tseletrack.data.network.userMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal enum class RegistrationAccountType {
    Student,
    Organization,
}

internal enum class OrganizationRole {
    Company,
    University,
    Partner,
    Other,
}

internal data class RegistrationUiState(
    val accountType: RegistrationAccountType = RegistrationAccountType.Student,
    val isAccountTypeMenuOpen: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val acceptsPersonalData: Boolean = false,
    val contactName: String = "",
    val organizationName: String = "",
    val organizationEmail: String = "",
    val phone: String = "",
    val organizationRole: OrganizationRole = OrganizationRole.Company,
    val requestPurpose: String = "",
    val isDocumentAttached: Boolean = false,
    val acceptsOrganizationData: Boolean = false,
    val isOrganizationRequestSent: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

internal class RegistrationViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _registrationCompleted = Channel<Role>(Channel.BUFFERED)
    val registrationCompleted = _registrationCompleted.receiveAsFlow()

    fun setAccountTypeMenuOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isAccountTypeMenuOpen = isOpen) }
    }

    fun selectAccountType(accountType: RegistrationAccountType) {
        _uiState.update {
            it.copy(
                accountType = accountType,
                isAccountTypeMenuOpen = false,
                errorMessage = null,
            )
        }
    }

    fun updateFullName(value: String) = update { copy(fullName = value) }

    fun updateEmail(value: String) = update { copy(email = value) }

    fun updatePassword(value: String) = update { copy(password = value) }

    fun updateRepeatedPassword(value: String) = update { copy(repeatedPassword = value) }

    fun togglePersonalDataAgreement() = update {
        copy(acceptsPersonalData = !acceptsPersonalData)
    }

    fun updateContactName(value: String) = update { copy(contactName = value) }

    fun updateOrganizationName(value: String) = update { copy(organizationName = value) }

    fun updateOrganizationEmail(value: String) = update { copy(organizationEmail = value) }

    fun updatePhone(value: String) = update { copy(phone = value) }

    fun selectOrganizationRole(role: OrganizationRole) = update {
        copy(organizationRole = role)
    }

    fun updateRequestPurpose(value: String) = update { copy(requestPurpose = value) }

    fun toggleDocumentAttachment() = update {
        copy(isDocumentAttached = !isDocumentAttached)
    }

    fun toggleOrganizationDataAgreement() = update {
        copy(acceptsOrganizationData = !acceptsOrganizationData)
    }

    fun submit() {
        if (_uiState.value.isSubmitting) return
        when (_uiState.value.accountType) {
            RegistrationAccountType.Student -> registerStudent()
            RegistrationAccountType.Organization -> submitOrganizationRequest()
        }
    }

    fun editOrganizationRequest() {
        _uiState.update {
            it.copy(isOrganizationRequestSent = false, errorMessage = null)
        }
    }

    private fun registerStudent() {
        val state = _uiState.value
        val validationError = validateStudent(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
                val role = authRepository.registerStudent(
                    fullName = state.fullName,
                    email = state.email,
                    password = state.password,
                )
                _registrationCompleted.send(role)
            } catch (error: Exception) {
                _uiState.update { it.copy(errorMessage = error.userMessage()) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun submitOrganizationRequest() {
        val validationError = validateOrganization(_uiState.value)
        _uiState.update {
            if (validationError == null) {
                it.copy(isOrganizationRequestSent = true, errorMessage = null)
            } else {
                it.copy(errorMessage = validationError)
            }
        }
    }

    private fun update(transform: RegistrationUiState.() -> RegistrationUiState) {
        _uiState.update { state -> state.transform().copy(errorMessage = null) }
    }

    private fun validateStudent(state: RegistrationUiState): String? = when {
        state.fullName.trim().length < 2 -> "Введите фамилию и имя."
        !state.email.isValidEmail() -> "Введите корректный email."
        state.password.length < MIN_PASSWORD_LENGTH ->
            "Пароль должен содержать не менее $MIN_PASSWORD_LENGTH символов."
        state.password != state.repeatedPassword -> "Пароли не совпадают."
        !state.acceptsPersonalData -> "Подтвердите согласие на обработку персональных данных."
        else -> null
    }

    private fun validateOrganization(state: RegistrationUiState): String? = when {
        state.contactName.trim().length < 2 -> "Введите имя контактного лица."
        state.organizationName.isBlank() -> "Введите название организации."
        !state.organizationEmail.isValidEmail() -> "Введите корректный email для ответа."
        state.phone.isBlank() -> "Введите телефон."
        state.requestPurpose.isBlank() -> "Опишите цель заявки."
        !state.acceptsOrganizationData -> "Подтвердите согласие на обработку данных."
        else -> null
    }

    private fun String.isValidEmail(): Boolean {
        val atIndex = indexOf('@')
        val dotIndex = lastIndexOf('.')
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < lastIndex
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
