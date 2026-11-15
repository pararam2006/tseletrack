package com.pararam2006.tseletrack.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.BuildConfig
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.ui.common.PasswordVisibilityButton
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (Role) -> Unit,
    onRegistrationClick: () -> Unit,
    onCareerGuidanceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<LoginViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.authenticatedRoles.collect(onLoginSuccess)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tseletrackBg)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimension.Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(Dimension.Layout.loginLogoTop))

        BrandMark()

        Spacer(modifier = Modifier.height(Dimension.Spacing.huge))

        Text(
            text = stringResource(R.string.login_title),
            color = tseletrackInk,
            fontSize = 24.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = stringResource(R.string.login_subtitle),
            color = tseletrackMut,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(modifier = Modifier.height(Dimension.Layout.loginFormTop))

        LoginTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            placeholder = stringResource(R.string.login_email),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
        )

        Spacer(modifier = Modifier.height(Dimension.Spacing.large))

        LoginTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            placeholder = stringResource(R.string.login_password),
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            ),
            trailingContent = {
                PasswordVisibilityButton(
                    isPasswordVisible = isPasswordVisible,
                    onClick = { isPasswordVisible = !isPasswordVisible },
                )
            },
        )

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(Dimension.Spacing.mediumLarge))
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                color = tseletrackRed,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }

        Spacer(modifier = Modifier.height(Dimension.Spacing.xxxl))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.large)
                .background(tseletrackRed)
                .clickable {
                    focusManager.clearFocus()
                    viewModel.login()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.login_action),
                color = tseletrackWhite,
                style = AppTextStyles.bodyMediumStrong,
            )
        }

        if (BuildConfig.DEBUG) {
            Spacer(modifier = Modifier.height(Dimension.Spacing.large))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(tseletrackWhite)
                    .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
                    .clickable(enabled = !state.isSubmitting, onClick = viewModel::startStudentDemo),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.login_student_demo),
                    color = tseletrackInk,
                    style = AppTextStyles.bodyCompactStrong,
                )
            }

            Spacer(modifier = Modifier.height(Dimension.Spacing.small))

//            Text(
//                text = stringResource(R.string.login_student_demo_hint),
//                color = tseletrackMut,
//                style = MaterialTheme.typography.bodySmall,
//            )

            Spacer(modifier = Modifier.height(Dimension.Spacing.small))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(tseletrackWhite)
                    .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
                    .clickable(enabled = !state.isSubmitting, onClick = viewModel::startUniversityDemo),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.login_university_demo),
                    color = tseletrackInk,
                    style = AppTextStyles.bodyCompactStrong,
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimension.Spacing.xxl))

        Text(
            text = stringResource(R.string.login_registration),
            modifier = Modifier
                .clip(AppShapes.checkbox)
                .clickable(onClick = onRegistrationClick)
                .padding(horizontal = Dimension.Spacing.medium, vertical = Dimension.Spacing.xs),
            color = tseletrackRed,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(Dimension.Spacing.largeExtra))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.large)
                .background(tseletrackWhite)
                .border(Dimension.Size.stroke, tseletrackRed, MaterialTheme.shapes.large)
                .clickable(onClick = onCareerGuidanceClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.login_career_guidance),
                color = tseletrackRed,
                style = AppTextStyles.bodyCompactStrong,
            )
        }

        Spacer(modifier = Modifier.height(Dimension.Spacing.xxl))
    }
}

@Composable
private fun BrandMark() {
    Image(
        painter = painterResource(R.drawable.ic_login_brand),
        contentDescription = null,
        modifier = Modifier
            .size(width = 100.dp, height = 104.dp)
            .clip(AppShapes.logo),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = tseletrackInk),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(tseletrackRed),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(tseletrackWhite)
                    .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.large)
                    .padding(
                        start = Dimension.Spacing.extraLarge,
                        end = if (trailingContent == null) {
                            Dimension.Spacing.extraLarge
                        } else {
                            Dimension.Spacing.xs
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = tseletrackMut,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                        )
                    }

                    innerTextField()
                }
                trailingContent?.invoke()
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    TseletrackTheme(dynamicColor = false) {
        LoginScreen(
            onLoginSuccess = {},
            onRegistrationClick = {},
            onCareerGuidanceClick = {},
        )
    }
}
