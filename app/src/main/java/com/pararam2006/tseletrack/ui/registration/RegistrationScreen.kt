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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.core.Role as UserRole
import com.pararam2006.tseletrack.ui.common.PasswordVisibilityButton
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
            .padding(horizontal = Dimension.Spacing.xxl),
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
        Spacer(Modifier.width(Dimension.Spacing.medium))
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
            .padding(horizontal = Dimension.Spacing.xxl),
    ) {
        Spacer(Modifier.height(Dimension.Spacing.extraLarge))
        AccountTypePrompt(
            selectedType = state.accountType,
            onClick = { viewModel.setAccountTypeMenuOpen(true) },
        )
        Spacer(Modifier.height(Dimension.Spacing.extraLarge))
        RegistrationTextField(
            value = state.fullName,
            onValueChange = viewModel::updateFullName,
            placeholder = stringResource(R.string.registration_full_name),
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        RegistrationTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            placeholder = stringResource(R.string.login_email),
            keyboardType = KeyboardType.Email,
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        RegistrationPasswordField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            placeholder = stringResource(R.string.login_password),
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        RegistrationPasswordField(
            value = state.repeatedPassword,
            onValueChange = viewModel::updateRepeatedPassword,
            placeholder = stringResource(R.string.registration_repeat_password),
        )
        Spacer(Modifier.height(Dimension.Spacing.space18))
        RegistrationAgreement(
            checked = state.acceptsPersonalData,
            text = stringResource(R.string.registration_personal_data_agreement),
            onClick = viewModel::togglePersonalDataAgreement,
        )
        RegistrationError(state.errorMessage)
        Spacer(Modifier.height(Dimension.Spacing.largeExtra))
        RegistrationPrimaryButton(
            text = stringResource(R.string.registration_submit),
            onClick = viewModel::submit,
            isLoading = state.isSubmitting,
        )
        Spacer(Modifier.height(Dimension.Spacing.large))
        LoginLink(onClick = onLoginClick)
        Spacer(Modifier.height(Dimension.Spacing.xxxl))
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
            .padding(horizontal = Dimension.Spacing.xxl),
    ) {
        Spacer(Modifier.height(Dimension.Spacing.largeExtra))
        AccountTypePrompt(
            selectedType = state.accountType,
            onClick = { viewModel.setAccountTypeMenuOpen(true) },
        )
        Spacer(Modifier.height(Dimension.Spacing.large))
        Text(
            text = stringResource(R.string.registration_request_info),
            modifier = Modifier
                .fillMaxWidth()
                .background(tseletrackInfoTint, MaterialTheme.shapes.large)
                .padding(horizontal = Dimension.Spacing.largeExtra, vertical = Dimension.Spacing.medium),
            color = tseletrackInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
        Spacer(Modifier.height(Dimension.Spacing.large))
        RegistrationTextField(
            value = state.contactName,
            onValueChange = viewModel::updateContactName,
            placeholder = stringResource(R.string.registration_contact_name),
            height = 44.dp,
        )
        Spacer(Modifier.height(Dimension.Spacing.xs))
        RegistrationTextField(
            value = state.organizationName,
            onValueChange = viewModel::updateOrganizationName,
            placeholder = stringResource(R.string.registration_organization),
            height = 44.dp,
        )
        Spacer(Modifier.height(Dimension.Spacing.xs))
        RegistrationTextField(
            value = state.organizationEmail,
            onValueChange = viewModel::updateOrganizationEmail,
            placeholder = stringResource(R.string.registration_reply_email),
            keyboardType = KeyboardType.Email,
            height = 44.dp,
        )
        Spacer(Modifier.height(Dimension.Spacing.xs))
        RegistrationTextField(
            value = state.phone,
            onValueChange = viewModel::updatePhone,
            placeholder = stringResource(R.string.registration_phone),
            keyboardType = KeyboardType.Phone,
            height = 44.dp,
        )
        Spacer(Modifier.height(Dimension.Spacing.small))
        Text(
            text = stringResource(R.string.registration_platform_role),
            color = tseletrackInk,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(Dimension.Spacing.xs))
        OrganizationRoleSelector(
            selectedRole = state.organizationRole,
            onRoleSelected = viewModel::selectOrganizationRole,
        )
        Spacer(Modifier.height(Dimension.Spacing.large))
        RegistrationTextField(
            value = state.requestPurpose,
            onValueChange = viewModel::updateRequestPurpose,
            placeholder = stringResource(R.string.registration_purpose),
            height = 72.dp,
            singleLine = false,
        )
        Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(tseletrackWhite, MaterialTheme.shapes.medium)
                .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.medium)
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
        Spacer(Modifier.height(Dimension.Spacing.largeExtra))
        RegistrationAgreement(
            checked = state.acceptsOrganizationData,
            text = stringResource(R.string.registration_organization_data_agreement),
            onClick = viewModel::toggleOrganizationDataAgreement,
        )
        RegistrationError(state.errorMessage)
        Spacer(Modifier.height(Dimension.Spacing.largeExtra))
        RegistrationPrimaryButton(
            text = stringResource(R.string.registration_send_request),
            onClick = viewModel::submit,
            isLoading = false,
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        LoginLink(onClick = onLoginClick)
        Spacer(Modifier.height(Dimension.Spacing.xxxl))
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
    Spacer(Modifier.height(Dimension.Spacing.space5))
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
            .background(tseletrackWhite, MaterialTheme.shapes.large)
            .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.registration_account_type_description),
                onClick = onClick,
            )
            .padding(horizontal = Dimension.Spacing.extraLarge),
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
                style = AppTextStyles.bodyMediumEmphasis,
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
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.extraLarge),
        ) {
            AccountTypePrompt(
                selectedType = selectedType,
                onClick = onDismiss,
            )
            Spacer(Modifier.height(Dimension.Spacing.medium))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackWhite, MaterialTheme.shapes.large)
                    .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
                    .padding(Dimension.Spacing.medium),
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
                shape = AppShapes.outlinedButton,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Dimension.Spacing.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = tseletrackInk,
                style = AppTextStyles.bodyMediumEmphasis,
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
            PasswordVisibilityButton(
                isPasswordVisible = isPasswordVisible,
                onClick = { isPasswordVisible = !isPasswordVisible },
            )
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
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = tseletrackInk),
        cursorBrush = SolidColor(tseletrackRed),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tseletrackWhite, MaterialTheme.shapes.large)
                    .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
                    .padding(start = Dimension.Spacing.extraLarge, end = if (trailingContent == null) Dimension.Spacing.extraLarge else Dimension.Spacing.medium),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(if (singleLine) Modifier else Modifier.padding(top = Dimension.Spacing.mediumLarge)),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = tseletrackMut,
                            style = MaterialTheme.typography.bodyMedium,
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
                    shape = MaterialTheme.shapes.extraSmall,
                )
                .border(Dimension.Size.strokeSelected, tseletrackRed, MaterialTheme.shapes.extraSmall),
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
        Spacer(Modifier.width(Dimension.Spacing.mediumLarge))
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
    Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
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
            .background(tseletrackRed, MaterialTheme.shapes.large)
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
                style = AppTextStyles.bodyMediumStrong,
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
            .padding(vertical = Dimension.Spacing.xs),
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
    Row(horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium)) {
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
                shape = AppShapes.pill,
            )
            .border(
                width = 1.dp,
                color = if (selected) tseletrackRed else tseletrackLine,
                shape = AppShapes.pill,
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
            .padding(horizontal = Dimension.Spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Dimension.Layout.registrationResultTop))
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
        Spacer(Modifier.height(Dimension.Spacing.space18))
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
        Spacer(Modifier.height(Dimension.Layout.registrationResultContent))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(tseletrackWhite, MaterialTheme.shapes.extraLarge)
                .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.extraLarge)
                .padding(horizontal = Dimension.Spacing.space18, vertical = Dimension.Spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xxl),
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
        Spacer(Modifier.height(Dimension.Spacing.medium))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(tseletrackWhite, MaterialTheme.shapes.large)
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
        Spacer(Modifier.height(Dimension.Spacing.xxxl))
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
        Spacer(Modifier.width(Dimension.Spacing.largeExtra))
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
