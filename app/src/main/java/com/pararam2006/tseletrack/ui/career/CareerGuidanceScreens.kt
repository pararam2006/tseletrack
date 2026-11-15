package com.pararam2006.tseletrack.ui.career

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.career.CareerGuidanceContent
import com.pararam2006.tseletrack.domain.career.CareerGuidanceResult
import com.pararam2006.tseletrack.domain.career.CareerMatch
import com.pararam2006.tseletrack.ui.common.AppProgressBar
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
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

@Composable
internal fun CareerGuidanceLandingScreen(
    state: CareerGuidanceUiState,
    onBack: () -> Unit,
    onStart: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
) {
    val hasProgress = state.hasSavedProgress
    CareerScaffold(
        title = stringResource(R.string.career_title),
        onBack = onBack,
        actions = {
            CareerPrimaryButton(
                text = stringResource(if (hasProgress) R.string.career_continue else R.string.career_start),
                onClick = if (hasProgress) onResume else onStart,
            )
            if (hasProgress) {
                Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
                CareerSecondaryButton(
                    text = stringResource(R.string.career_restart),
                    onClick = onRestart,
                )
            }
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.space22),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.largeExtra),
        ) {
            HeroResourceIcon(
                iconRes = if (hasProgress) R.drawable.ic_career_schedule else R.drawable.ic_career_radio_checked,
                background = if (hasProgress) tseletrackWarnTint else tseletrackRedTint,
                tint = if (hasProgress) tseletrackWarn else tseletrackRed,
            )
            if (!hasProgress) {
                LabelChip(
                    text = stringResource(R.string.career_without_registration),
                    background = tseletrackOkTint,
                    foreground = tseletrackOk,
                )
            }
            Text(
                text = stringResource(if (hasProgress) R.string.career_resume_title else R.string.career_intro_title),
                color = tseletrackInk,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (hasProgress) {
                    stringResource(R.string.career_resume_description, state.questionIndex + 1)
                } else {
                    stringResource(R.string.career_intro_description)
                },
                color = tseletrackMut,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
            if (hasProgress) {
                SavedProgressCard(answeredCount = state.answers.size)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium)) {
                    LabelChip(stringResource(R.string.career_questions_fact))
                    LabelChip(stringResource(R.string.career_time_fact))
                    LabelChip(stringResource(R.string.career_offline_fact))
                }
            }
            InfoPanel(
                title = stringResource(if (hasProgress) R.string.career_answers_saved else R.string.career_works_offline),
                description = stringResource(
                    if (hasProgress) R.string.career_answers_saved_description
                    else R.string.career_works_offline_description,
                ),
            )
        }
    }
}

@Composable
internal fun CareerQuestionScreen(
    state: CareerGuidanceUiState,
    onBack: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
) {
    val question = state.currentQuestion ?: return
    val questionNumber = state.questionIndex + 1
    val totalQuestions = CareerGuidanceContent.questions.size
    CareerScaffold(
        title = stringResource(R.string.career_question_title, questionNumber, totalQuestions),
        onBack = onBack,
        actions = {
            CareerPrimaryButton(
                text = stringResource(
                    if (state.questionIndex == CareerGuidanceContent.questions.lastIndex) {
                        R.string.career_show_result
                    } else {
                        R.string.career_next
                    },
                ),
                onClick = onNext,
                enabled = state.canContinue,
            )
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.space22),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.largeExtra),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.career_title),
                    color = tseletrackMut,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = stringResource(R.string.career_progress_count, questionNumber, totalQuestions),
                    color = tseletrackRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            AppProgressBar(
                percent = questionNumber * 100 / totalQuestions,
                height = Dimension.Size.progressLarge,
            )
            Text(
                text = question.title,
                color = tseletrackInk,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = if (question.maxSelections == 1) {
                    stringResource(R.string.career_choose_one)
                } else {
                    stringResource(R.string.career_choose_up_to, question.maxSelections)
                },
                color = tseletrackMut,
                style = MaterialTheme.typography.bodyMedium,
            )
            question.options.forEach { option ->
                CareerOptionCard(
                    title = option.title,
                    selected = option.id in state.selectedOptionIds,
                    multiple = question.maxSelections > 1,
                    onClick = { onOptionSelected(option.id) },
                )
            }
        }
    }
}

@Composable
internal fun CareerResultScreen(
    result: CareerGuidanceResult,
    isAuthenticated: Boolean,
    isSyncing: Boolean,
    syncError: String?,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onRestart: () -> Unit,
) {
    val primary = result.primaryMatch
    CareerScaffold(
        title = stringResource(R.string.career_result_title),
        onBack = onBack,
        actions = {
            syncError?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(bottom = Dimension.Spacing.mediumLarge),
                    color = tseletrackRed,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
            CareerPrimaryButton(
                text = stringResource(
                    if (isAuthenticated) R.string.career_add_to_route
                    else R.string.career_save_result,
                ),
                onClick = onSave,
                loading = isSyncing,
            )
            Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
            CareerSecondaryButton(
                text = stringResource(R.string.career_restart),
                onClick = onRestart,
                enabled = !isSyncing,
            )
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.space22),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackRedTint, AppShapes.hero)
                    .padding(Dimension.Spacing.space18),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
            ) {
                LabelChip(
                    text = stringResource(R.string.career_your_profile),
                    foreground = tseletrackRed,
                )
                Text(
                    text = primary.direction.profileTitle,
                    color = tseletrackInk,
                    fontSize = 23.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = primary.direction.profileDescription,
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

            SectionTitle(stringResource(R.string.career_suitable_directions))
            result.matches.forEach { CareerMatchCard(it) }

            SectionTitle(stringResource(R.string.career_strengths))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
            ) {
                primary.direction.strengths.forEach { LabelChip(it) }
            }

            TargetAdmissionNotice()
            InfoPanel(
                title = stringResource(R.string.career_saved_locally),
                description = stringResource(R.string.career_saved_locally_description),
            )
        }
    }
}

@Composable
internal fun CareerAuthGateScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onStayGuest: () -> Unit,
) {
    CareerScaffold(
        title = stringResource(R.string.career_save_title),
        onBack = onBack,
        actions = {
            CareerPrimaryButton(
                text = stringResource(R.string.career_login_action),
                onClick = onLogin,
            )
            Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
            CareerSecondaryButton(
                text = stringResource(R.string.career_stay_guest),
                onClick = onStayGuest,
            )
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.space22),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.largeExtra),
        ) {
            HeroIcon(Icons.Default.AccountCircle, tseletrackInfoTint, tseletrackInfo)
            Text(
                text = stringResource(R.string.career_add_result_title),
                color = tseletrackInk,
                fontSize = 25.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.career_account_explanation),
                color = tseletrackMut,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackWhite, AppShapes.cardLarge)
                    .border(Dimension.Size.stroke, tseletrackLine, AppShapes.cardLarge)
                    .padding(Dimension.Padding.screen),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
            ) {
                BenefitRow(stringResource(R.string.career_benefit_sync))
                BenefitRow(stringResource(R.string.career_benefit_recommendations))
                BenefitRow(stringResource(R.string.career_benefit_complete_stage))
            }
            InfoPanel(
                title = stringResource(R.string.career_login_later),
                description = stringResource(R.string.career_login_later_description),
            )
        }
    }
}

@Composable
internal fun CareerSyncedScreen(
    result: CareerGuidanceResult,
    onBack: () -> Unit,
    onRoute: () -> Unit,
    onRecommendations: () -> Unit,
) {
    val primary = result.primaryMatch
    CareerScaffold(
        title = stringResource(R.string.career_title),
        onBack = onBack,
        actions = {
            CareerPrimaryButton(
                text = stringResource(R.string.career_back_to_route),
                onClick = onRoute,
            )
            Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
            CareerSecondaryButton(
                text = stringResource(R.string.career_view_recommendations),
                onClick = onRecommendations,
            )
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.space22),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.largeExtra),
        ) {
            HeroIcon(Icons.Default.Check, tseletrackOkTint, tseletrackOk)
            Text(
                text = stringResource(R.string.career_result_saved),
                color = tseletrackInk,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.career_profile_added, primary.direction.profileTitle),
                color = tseletrackMut,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
            SuccessPanel()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackWhite, AppShapes.cardLarge)
                    .border(Dimension.Size.stroke, tseletrackLine, AppShapes.cardLarge)
                    .padding(Dimension.Padding.screen),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
            ) {
                Text(
                    text = stringResource(R.string.career_your_profile),
                    color = tseletrackMut,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = primary.direction.profileTitle,
                    color = tseletrackInk,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.career_best_match,
                        primary.direction.title,
                        primary.percent,
                    ),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackInfoTint, AppShapes.cardLarge)
                    .padding(Dimension.Padding.screen),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.small),
            ) {
                Text(
                    text = stringResource(R.string.career_next_stage),
                    color = tseletrackInfo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.career_university_choice),
                    color = tseletrackInk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.career_university_choice_description),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun CareerScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = tseletrackBg,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = { CareerHeader(title, onBack) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tseletrackBg)
                    .navigationBarsPadding()
                    .padding(horizontal = Dimension.Spacing.xxl, vertical = Dimension.Spacing.large),
            ) {
                actions()
            }
        },
    ) { paddingValues ->
        content(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun CareerHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackWhite)
            .statusBarsPadding()
            .height(48.dp)
            .padding(horizontal = Dimension.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.registration_back_description),
                tint = tseletrackInk,
            )
        }
        Text(
            text = title,
            color = tseletrackInk,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CareerPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(if (enabled) tseletrackRed else tseletrackLine, MaterialTheme.shapes.extraLarge)
            .clickable(enabled = enabled && !loading, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = tseletrackWhite,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                color = if (enabled) tseletrackWhite else tseletrackMut,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun CareerSecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(tseletrackWhite, MaterialTheme.shapes.extraLarge)
            .border(
                Dimension.Size.stroke,
                if (enabled) tseletrackRed else tseletrackLine,
                MaterialTheme.shapes.extraLarge,
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) tseletrackRed else tseletrackMut,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun HeroIcon(icon: ImageVector, background: Color, tint: Color) {
    Box(
        modifier = Modifier
            .size(82.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(38.dp),
            tint = tint,
        )
    }
}

@Composable
private fun HeroResourceIcon(iconRes: Int, background: Color, tint: Color) {
    Box(
        modifier = Modifier
            .size(82.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(38.dp),
            tint = tint,
        )
    }
}

@Composable
private fun LabelChip(
    text: String,
    background: Color = tseletrackWhite,
    foreground: Color = tseletrackInk,
) {
    Box(
        modifier = Modifier
            .background(background, CircleShape)
            .padding(horizontal = Dimension.Spacing.mediumLarge, vertical = Dimension.Spacing.space5),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun InfoPanel(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackInfoTint, MaterialTheme.shapes.extraLarge)
            .padding(Dimension.Spacing.largeExtra),
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_career_wifi_off),
            contentDescription = null,
            tint = tseletrackInfo,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xxs)) {
            Text(
                text = title,
                color = tseletrackInk,
                style = AppTextStyles.bodyMediumStrong,
            )
            Text(
                text = description,
                color = tseletrackMut,
                style = AppTextStyles.bodyCompact,
            )
        }
    }
}

@Composable
private fun SavedProgressCard(answeredCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackWhite, AppShapes.cardLarge)
            .border(Dimension.Size.stroke, tseletrackLine, AppShapes.cardLarge)
            .padding(Dimension.Padding.screen),
        verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.career_completed), color = tseletrackMut, fontSize = 14.sp)
            Text(
                text = stringResource(
                    R.string.career_progress_count,
                    answeredCount,
                    CareerGuidanceContent.questions.size,
                ),
                color = tseletrackInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        AppProgressBar(
            percent = answeredCount * 100 / CareerGuidanceContent.questions.size,
            height = Dimension.Size.progressLarge,
        )
    }
}

@Composable
private fun CareerOptionCard(
    title: String,
    selected: Boolean,
    multiple: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(if (selected) tseletrackRedTint else tseletrackWhite, MaterialTheme.shapes.extraLarge)
            .border(if (selected) Dimension.Size.strokeSelected else Dimension.Size.stroke, if (selected) tseletrackRed else tseletrackLine, MaterialTheme.shapes.extraLarge)
            .clickable(
                role = if (multiple) Role.Checkbox else Role.RadioButton,
                onClick = onClick,
            )
            .padding(horizontal = Dimension.Spacing.medium, vertical = Dimension.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (multiple) {
            Checkbox(
                checked = selected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = tseletrackRed,
                    uncheckedColor = tseletrackMut,
                    checkmarkColor = tseletrackWhite,
                ),
            )
        } else {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = tseletrackRed,
                    unselectedColor = tseletrackMut,
                ),
            )
        }
        Spacer(Modifier.width(Dimension.Spacing.xs))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = tseletrackInk,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        color = tseletrackInk,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun CareerMatchCard(match: CareerMatch) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackWhite, MaterialTheme.shapes.extraLarge)
            .border(Dimension.Size.stroke, tseletrackLine, MaterialTheme.shapes.extraLarge)
            .padding(Dimension.Spacing.largeExtra),
        verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = match.direction.title,
                modifier = Modifier.weight(1f),
                color = tseletrackInk,
                style = AppTextStyles.cardTitle,
            )
            Spacer(Modifier.width(Dimension.Spacing.large))
            Text(
                text = "${match.percent}%",
                color = tseletrackRed,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = match.direction.institute,
            color = tseletrackInfo,
            style = AppTextStyles.bodyCompactStrong,
        )
        MatchList(
            title = stringResource(R.string.career_programs),
            values = match.direction.programs,
        )
        MatchList(
            title = stringResource(R.string.career_professions),
            values = match.direction.professions,
        )
    }
}

@Composable
private fun MatchList(title: String, values: List<String>) {
    Text(
        text = title,
        color = tseletrackInk,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
    Text(
        text = values.joinToString(separator = "\n") { "• $it" },
        color = tseletrackMut,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun TargetAdmissionNotice() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackWarnTint, MaterialTheme.shapes.extraLarge)
            .padding(Dimension.Spacing.largeExtra),
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = tseletrackWarn)
        Column(verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xs)) {
            Text(
                text = stringResource(R.string.career_target_notice_title),
                color = tseletrackInk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.career_target_notice_description),
                color = tseletrackMut,
                style = AppTextStyles.bodyCompact,
            )
            Text(
                text = stringResource(R.string.career_target_customers),
                color = tseletrackInk,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = tseletrackOk,
        )
        Text(
            text = text,
            color = tseletrackInk,
            style = AppTextStyles.bodyMediumEmphasis,
        )
    }
}

@Composable
private fun SuccessPanel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tseletrackOkTint, MaterialTheme.shapes.extraLarge)
            .padding(Dimension.Spacing.largeExtra),
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = tseletrackOk)
        Column(verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xxs)) {
            Text(
                text = stringResource(R.string.career_stage_completed),
                color = tseletrackOk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.career_synced_description),
                color = tseletrackInk,
                style = AppTextStyles.bodyCompact,
            )
        }
    }
}
