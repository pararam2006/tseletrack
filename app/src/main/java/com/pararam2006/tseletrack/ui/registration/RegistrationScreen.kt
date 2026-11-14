package com.pararam2006.tseletrack.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.core.Role as UserRole
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegistrationScreen(
    onBack: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationSuccess: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<RegistrationViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.registrationCompleted.collect(onRegistrationSuccess)
    }

    if (state.isOrganizationRequestSent) {
        RequestSentContent(
            onLoginClick = onLoginClick,
            onEditClick = viewModel::editOrganizationRequest,
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tseletrackBg)
            .imePadding(),
    ) {
        RegistrationTopBar(
            title = stringResource(
                if (state.accountType == RegistrationAccountType.Student) {
                    R.string.registration_title
                } else {
                    R.string.registration_request_title
                },
            ),
            onBack = onBack,
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (state.accountType) {
                RegistrationAccountType.Student -> StudentRegistrationForm(
                    state = state,
                    viewModel = viewModel,
                    onLoginClick = onLoginClick,
                )

                RegistrationAccountType.Organization -> OrganizationRequestForm(
                    state = state,
                    viewModel = viewModel,
                    onLoginClick = onLoginClick,
                )
            }

            if (state.isAccountTypeMenuOpen) {
                AccountTypeMenuOverlay(
                    selectedType = state.accountType,
                    onTypeSelected = viewModel::selectAccountType,
                    onDismiss = { viewModel.setAccountTypeMenuOpen(false) },
                )
            }
        }
    }
}

@Composable
private fun RegistrationTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackWhite)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(R.string.registration_back_description),
                    onClick = onBack,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_registration_back),
                contentDescription = stringResource(R.string.registration_back_description),
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            color = tseletrackInk,
            fontSize = 20.sp,
            lineHeight = 27.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun StudentRegistrationForm(
    state: RegistrationUiState,
    viewModel: RegistrationViewModel,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        AccountTypePrompt(
            selectedType = state.accountType,
            onClick = { viewModel.setAccountTypeMenuOpen(true) },
        )
        Spacer(Modifier.height(16.dp))
        RegistrationTextField(
            value = state.fullName,
            onValueChange = viewModel::updateFullName,
            placeholder = stringResource(R.string.registration_full_name),
        )
        Spacer(Modifier.height(8.dp))
        RegistrationTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            placeholder = stringResource(R.string.login_email),
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(8.dp))
        RegistrationPasswordField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            placeholder = stringResource(R.string.login_password),
        )
        Spacer(Modifier.height(8.dp))
        RegistrationTextField(
            value = state.repeatedPassword,
            onValueChange = viewModel::updateRepeatedPassword,
            placeholder = stringResource(R.string.registration_repeat_password),
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(Modifier.height(18.dp))
        RegistrationAgreement(
            checked = state.acceptsPersonalData,
            text = stringResource(R.string.registration_personal_data_agreement),
            onClick = viewModel::togglePersonalDataAgreement,
        )
        RegistrationError(state.errorMessage)
        Spacer(Modifier.height(14.dp))
        RegistrationPrimaryButton(
            text = stringResource(R.string.registration_submit),
            onClick = viewModel::submit,
            isLoading = state.isSubmitting,
        )
        Spacer(Modifier.height(12.dp))
        LoginLink(onClick = onLoginClick)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun OrganizationRequestForm(
    state: RegistrationUiState,
    viewModel: RegistrationViewModel,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(14.dp))
        AccountTypePrompt(
            selectedType = state.accountType,
            onClick = { viewModel.setAccountTypeMenuOpen(true) },
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.registration_request_info),
            modifier = Modifier
                .fillMaxWidth()
                .background(tseletrackInfoTint, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            color = tseletrackInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
        Spacer(Modifier.height(12.dp))
        RegistrationTextField(
            value = state.contactName,
            onValueChange = viewModel::updateContactName,
            placeholder = stringResource(R.string.registration_contact_name),
            height = 44.dp,
        )
        Spacer(Modifier.height(4.dp))
        RegistrationTextField(
            value = state.organizationName,
            onValueChange = viewModel::updateOrganizationName,
            placeholder = stringResource(R.string.registration_organization),
            height = 44.dp,
        )
        Spacer(Modifier.height(4.dp))
        RegistrationTextField(
            value = state.organizationEmail,
            onValueChange = viewModel::updateOrganizationEmail,
            placeholder = stringResource(R.string.registration_reply_email),
            keyboardType = KeyboardType.Email,
            height = 44.dp,
        )
        Spacer(Modifier.height(4.dp))
        RegistrationTextField(
            value = state.phone,
            onValueChange = viewModel::updatePhone,
            placeholder = stringResource(R.string.registration_phone),
            keyboardType = KeyboardType.Phone,
            height = 44.dp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.registration_platform_role),
            color = tseletrackInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        OrganizationRoleSelector(
            selectedRole = state.organizationRole,
            onRoleSelected = viewModel::selectOrganizationRole,
        )
        Spacer(Modifier.height(12.dp))
        RegistrationTextField(
            value = state.requestPurpose,
            onValueChange = viewModel::updateRequestPurpose,
            placeholder = stringResource(R.string.registration_purpose),
            height = 72.dp,
            singleLine = false,
        )
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(tseletrackWhite, RoundedCornerShape(10.dp))
                .border(1.dp, tseletrackLine, RoundedCornerShape(10.dp))
                .clickable(role = Role.Button, onClick = viewModel::toggleDocumentAttachment),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(
                    if (state.isDocumentAttached) {
                        R.string.registration_document_attached
                    } else {
                        R.string.registration_attach_document
                    },
                ),
                color = if (state.isDocumentAttached) tseletrackOk else tseletrackMut,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.height(14.dp))
        RegistrationAgreement(
            checked = state.acceptsOrganizationData,
            text = stringResource(R.string.registration_organization_data_agreement),
            onClick = viewModel::toggleOrganizationDataAgreement,
        )
        RegistrationError(state.errorMessage)
        Spacer(Modifier.height(14.dp))
        RegistrationPrimaryButton(
            text = stringResource(R.string.registration_send_request),
            onClick = viewModel::submit,
            isLoading = false,
        )
        Spacer(Modifier.height(8.dp))
        LoginLink(onClick = onLoginClick)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AccountTypePrompt(
    selectedType: RegistrationAccountType,
    onClick: () -> Unit,
) {
    Text(
        text = stringResource(R.string.registration_account_prompt),
        color = tseletrackInk,
        fontSize = 11.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(5.dp))
    AccountTypeField(
        accountType = selectedType,
        onClick = onClick,
    )
}

@Composable
private fun AccountTypeField(
    accountType: RegistrationAccountType,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(tseletrackWhite, RoundedCornerShape(12.dp))
            .border(1.dp, tseletrackLine, RoundedCornerShape(12.dp))
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.registration_account_type_description),
                onClick = onClick,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.registration_account_label),
                color = tseletrackMut,
                fontSize = 10.sp,
                lineHeight = 15.sp,
            )
            Text(
                text = stringResource(
                    if (accountType == RegistrationAccountType.Student) {
                        R.string.registration_account_student
                    } else {
                        R.string.registration_account_other
                    },
                ),
                color = tseletrackInk,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_registration_chevron),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(width = 12.dp, height = 6.dp),
        )
    }
}

@Composable
private fun AccountTypeMenuOverlay(
    selectedType: RegistrationAccountType,
    onTypeSelected: (RegistrationAccountType) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f))
                .clickable(onClick = onDismiss),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            AccountTypePrompt(
                selectedType = selectedType,
                onClick = onDismiss,
            )
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, tseletrackLine, RoundedCornerShape(12.dp))
                    .padding(8.dp),
            ) {
                AccountTypeMenuItem(
                    title = stringResource(R.string.registration_account_student),
                    subtitle = stringResource(R.string.registration_account_student_hint),
                    selected = selectedType == RegistrationAccountType.Student,
                    onClick = { onTypeSelected(RegistrationAccountType.Student) },
                )
                AccountTypeMenuItem(
                    title = stringResource(R.string.registration_account_other),
                    subtitle = stringResource(R.string.registration_account_other_hint),
                    selected = selectedType == RegistrationAccountType.Organization,
                    onClick = { onTypeSelected(RegistrationAccountType.Organization) },
                )
            }
        }
    }
}

@Composable
private fun AccountTypeMenuItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = if (selected) tseletrackRedTint else Color.Transparent,
                shape = RoundedCornerShape(9.dp),
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = tseletrackInk,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                color = tseletrackMut,
                fontSize = 10.sp,
                lineHeight = 16.sp,
            )
        }
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_registration_check),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(width = 16.dp, height = 12.dp),
            )
        }
    }
}

@Composable
private fun RegistrationPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    RegistrationTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(
                            R.string.registration_password_visibility_description,
                        ),
                    ) {
                        isPasswordVisible = !isPasswordVisible
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_registration_visibility),
                    contentDescription = stringResource(
                        R.string.registration_password_visibility_description,
                    ),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp),
                )
            }
        },
    )
}

@Composable
private fun RegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    height: Dp = 48.dp,
    singleLine: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        textStyle = TextStyle(
            color = tseletrackInk,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        cursorBrush = SolidColor(tseletrackRed),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tseletrackWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, tseletrackLine, RoundedCornerShape(12.dp))
                    .padding(start = 16.dp, end = if (trailingContent == null) 16.dp else 8.dp),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(if (singleLine) Modifier else Modifier.padding(top = 10.dp)),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = tseletrackMut,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                    }
                    innerTextField()
                }
                trailingContent?.invoke()
            }
        },
    )
}

@Composable
private fun RegistrationAgreement(
    checked: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(role = Role.Checkbox, onClick = onClick),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = if (checked) tseletrackRed else tseletrackWhite,
                    shape = RoundedCornerShape(6.dp),
                )
                .border(2.dp, tseletrackRed, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_registration_check),
                    contentDescription = null,
                    tint = tseletrackWhite,
                    modifier = Modifier.size(width = 12.dp, height = 9.dp),
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            color = tseletrackInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun RegistrationError(message: String?) {
    if (message == null) return
    Spacer(Modifier.height(10.dp))
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth(),
        color = tseletrackRed,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )
}

@Composable
private fun RegistrationPrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(tseletrackRed, RoundedCornerShape(12.dp))
            .clickable(enabled = !isLoading, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = tseletrackWhite,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                color = tseletrackWhite,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LoginLink(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.registration_login),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 4.dp),
        color = tseletrackRed,
        fontSize = 12.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun OrganizationRoleSelector(
    selectedRole: OrganizationRole,
    onRoleSelected: (OrganizationRole) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OrganizationRoleChip(
            text = stringResource(R.string.registration_role_company),
            selected = selectedRole == OrganizationRole.Company,
            width = 108.dp,
            onClick = { onRoleSelected(OrganizationRole.Company) },
        )
        OrganizationRoleChip(
            text = stringResource(R.string.registration_role_university),
            selected = selectedRole == OrganizationRole.University,
            width = 56.dp,
            onClick = { onRoleSelected(OrganizationRole.University) },
        )
        OrganizationRoleChip(
            text = stringResource(R.string.registration_role_partner),
            selected = selectedRole == OrganizationRole.Partner,
            width = 84.dp,
            onClick = { onRoleSelected(OrganizationRole.Partner) },
        )
        OrganizationRoleChip(
            text = stringResource(R.string.registration_role_other),
            selected = selectedRole == OrganizationRole.Other,
            width = 48.dp,
            onClick = { onRoleSelected(OrganizationRole.Other) },
        )
    }
}

@Composable
private fun OrganizationRoleChip(
    text: String,
    selected: Boolean,
    width: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(30.dp)
            .background(
                color = if (selected) tseletrackRedTint else tseletrackWhite,
                shape = RoundedCornerShape(15.dp),
            )
            .border(
                width = 1.dp,
                color = if (selected) tseletrackRed else tseletrackLine,
                shape = RoundedCornerShape(15.dp),
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) tseletrackRed else tseletrackInk,
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun RequestSentContent(
    onLoginClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tseletrackBg)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(88.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(tseletrackOkTint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_registration_success),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(width = 46.dp, height = 32.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.registration_sent_title),
            color = tseletrackInk,
            fontSize = 24.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.registration_sent_subtitle),
            color = tseletrackMut,
            fontSize = 12.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(23.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(tseletrackWhite, RoundedCornerShape(14.dp))
                .border(1.dp, tseletrackLine, RoundedCornerShape(14.dp))
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            RequestStatusRow(
                title = stringResource(R.string.registration_sent_title),
                subtitle = stringResource(R.string.registration_sent_now),
                dotColor = tseletrackOk,
                dotBackground = tseletrackOkTint,
            )
            RequestStatusRow(
                title = stringResource(R.string.registration_review_title),
                subtitle = stringResource(R.string.registration_review_time),
                dotColor = tseletrackInfo,
                dotBackground = tseletrackInfoTint,
            )
            RequestStatusRow(
                title = stringResource(R.string.registration_email_answer),
                subtitle = stringResource(R.string.registration_platform_access),
                dotColor = tseletrackMut,
                dotBackground = tseletrackLine,
            )
        }
        Spacer(Modifier.weight(1f))
        RegistrationPrimaryButton(
            text = stringResource(R.string.registration_to_login),
            onClick = onLoginClick,
            isLoading = false,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(tseletrackWhite, RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClick = onEditClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.registration_edit_request),
                color = tseletrackInk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RequestStatusRow(
    title: String,
    subtitle: String,
    dotColor: Color,
    dotBackground: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(dotBackground, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                color = tseletrackInk,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                color = tseletrackMut,
                fontSize = 10.sp,
                lineHeight = 16.sp,
            )
        }
    }
}
