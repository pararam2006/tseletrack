package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import com.pararam2006.tseletrack.domain.student.AcademicResult
import com.pararam2006.tseletrack.ui.common.AppBackScreenHeader
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppOutlinedButton
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
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
internal fun AcademicPerformanceScreen(
    state: AcademicPerformanceUiState,
    onRecordBookNumberChange: (String) -> Unit,
    onGroupTitleChange: (String) -> Unit,
    onConnect: () -> Unit,
    onRefresh: () -> Unit,
    onEditTracking: () -> Unit,
    onCancelEditing: () -> Unit,
    onBack: () -> Unit,
) {
    if (state.isLoading) {
        AppLoadingScreen(title = stringResource(R.string.academic_performance_title))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppBackScreenHeader(
            title = stringResource(R.string.academic_performance_title),
            onBack = onBack,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        ) {
            state.message?.let { message ->
                item {
                    StatusMessage(message, state.isMessageError)
                }
            }

            val performance = state.performance
            if (performance == null || state.isEditingTracking) {
                item {
                    TrackingForm(
                        state = state,
                        isEditing = performance != null,
                        onRecordBookNumberChange = onRecordBookNumberChange,
                        onGroupTitleChange = onGroupTitleChange,
                        onConnect = onConnect,
                        onCancel = onCancelEditing,
                    )
                }
            } else {
                item {
                    PerformanceSummary(
                        performance = performance,
                        isRefreshing = state.isRefreshing,
                        onRefresh = onRefresh,
                        onEditTracking = onEditTracking,
                    )
                }
                performance.warning?.let { warning ->
                    item { StatusMessage(warning, isError = true) }
                }
                if (performance.results.isEmpty()) {
                    item {
                        AppCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(R.string.academic_empty),
                                color = tseletrackMut,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    performance.results
                        .groupBy(AcademicResult::semester)
                        .toSortedMap()
                        .forEach { (semester, results) ->
                            item(key = "semester-$semester") {
                                SemesterCard(semester, results)
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun TrackingForm(
    state: AcademicPerformanceUiState,
    isEditing: Boolean,
    onRecordBookNumberChange: (String) -> Unit,
    onGroupTitleChange: (String) -> Unit,
    onConnect: () -> Unit,
    onCancel: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.academic_performance_intro_title),
            color = tseletrackInk,
            style = AppTextStyles.cardTitle,
        )
        Spacer(Modifier.height(Dimension.Spacing.small))
        Text(
            text = stringResource(R.string.academic_performance_intro_description),
            color = tseletrackMut,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(Dimension.Spacing.extraLarge))
        AcademicTextField(
            value = state.recordBookNumber,
            label = stringResource(R.string.academic_record_book_hint),
            onValueChange = onRecordBookNumberChange,
        )
        Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
        AcademicTextField(
            value = state.groupTitle,
            label = stringResource(R.string.academic_group_hint),
            onValueChange = onGroupTitleChange,
        )
        Spacer(Modifier.height(Dimension.Spacing.extraLarge))
        AppPrimaryButton(
            text = stringResource(
                if (state.isRefreshing) {
                    R.string.academic_connecting
                } else {
                    R.string.academic_connect
                },
            ),
            onClick = { if (!state.isRefreshing) onConnect() },
        )
        if (isEditing) {
            Spacer(Modifier.height(Dimension.Spacing.medium))
            AppOutlinedButton(
                text = stringResource(R.string.academic_cancel_edit),
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(Dimension.Spacing.medium))
        Text(
            text = stringResource(R.string.academic_privacy),
            color = tseletrackMut,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun AcademicTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = tseletrackRed,
            focusedLabelColor = tseletrackRed,
            unfocusedBorderColor = tseletrackLine,
            focusedContainerColor = tseletrackWhite,
            unfocusedContainerColor = tseletrackWhite,
        ),
    )
}

@Composable
private fun PerformanceSummary(
    performance: AcademicPerformance,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onEditTracking: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.academic_performance_intro_title),
                    color = tseletrackInk,
                    style = AppTextStyles.cardTitle,
                )
                Spacer(Modifier.height(Dimension.Spacing.small))
                Text(
                    text = stringResource(
                        R.string.academic_record_book_value,
                        performance.recordBookNumberMasked,
                    ),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = stringResource(R.string.academic_group_value, performance.groupTitle),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            SourceBadge(performance.isDemo)
        }
        Spacer(Modifier.height(Dimension.Spacing.extraLarge))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            PerformanceMetric(
                value = performance.results.map(AcademicResult::semester).distinct().size.toString(),
                label = stringResource(R.string.academic_semesters),
                modifier = Modifier.weight(1f),
            )
            PerformanceMetric(
                value = performance.results.size.toString(),
                label = stringResource(R.string.academic_results),
                modifier = Modifier.weight(1f),
            )
        }
        performance.fetchedAt?.let { fetchedAt ->
            Spacer(Modifier.height(Dimension.Spacing.medium))
            Text(
                text = stringResource(R.string.academic_updated, fetchedAt),
                color = tseletrackMut,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(Dimension.Spacing.large))
        AppOutlinedButton(
            text = stringResource(
                if (isRefreshing) R.string.academic_refreshing else R.string.academic_refresh,
            ),
            onClick = { if (!isRefreshing) onRefresh() },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        AppOutlinedButton(
            text = stringResource(R.string.academic_edit_tracking),
            onClick = onEditTracking,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SourceBadge(isDemo: Boolean) {
    val background = if (isDemo) tseletrackWarnTint else tseletrackInfoTint
    val foreground = if (isDemo) tseletrackWarn else tseletrackInfo
    Text(
        text = stringResource(
            if (isDemo) R.string.academic_demo_badge else R.string.academic_official_badge,
        ),
        modifier = Modifier
            .background(background, MaterialTheme.shapes.large)
            .padding(
                horizontal = Dimension.Spacing.mediumLarge,
                vertical = Dimension.Spacing.small,
            ),
        color = foreground,
        style = AppTextStyles.status,
    )
}

@Composable
private fun PerformanceMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(tseletrackBg, MaterialTheme.shapes.medium)
            .padding(Dimension.Spacing.large),
    ) {
        Text(
            text = value,
            color = tseletrackInk,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = label,
            color = tseletrackMut,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SemesterCard(
    semester: Int,
    results: List<AcademicResult>,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.academic_semester, semester),
            color = tseletrackInk,
            style = AppTextStyles.sectionTitle,
        )
        Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
        results.forEachIndexed { index, result ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimension.Spacing.large),
                    color = tseletrackLine,
                )
            }
            AcademicResultRow(result)
        }
    }
}

@Composable
private fun AcademicResultRow(result: AcademicResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.discipline,
                color = tseletrackInk,
                style = AppTextStyles.bodyCompactStrong,
            )
            result.points?.let { points ->
                Spacer(Modifier.height(Dimension.Spacing.xs))
                Text(
                    text = stringResource(R.string.academic_points, points),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            result.teacher?.let { teacher ->
                Text(
                    text = stringResource(R.string.academic_teacher, teacher),
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            result.retakeInfo?.let { retake ->
                Text(
                    text = stringResource(R.string.academic_retake, retake),
                    color = tseletrackRed,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (result.canBeRecredited) {
                Text(
                    text = stringResource(R.string.academic_recredit),
                    color = tseletrackWarn,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        GradeBadge(result.grade)
    }
}

@Composable
private fun GradeBadge(grade: String?) {
    val normalized = grade.orEmpty().lowercase()
    val colors: Pair<Color, Color> = when {
        normalized.contains("не зачтено") ||
            normalized.contains("неудовлетвор") ||
            normalized.contains("не аттестован") ->
            tseletrackRedTint to tseletrackRed
        normalized.contains("отлич") ||
            normalized.contains("зачтено") ||
            normalized.contains("аттестован") ->
            tseletrackOkTint to tseletrackOk
        normalized.contains("хорош") -> tseletrackInfoTint to tseletrackInfo
        normalized.contains("удовлетвор") && !normalized.contains("неудовлетвор") ->
            tseletrackWarnTint to tseletrackWarn
        else -> tseletrackLine to tseletrackMut
    }
    Text(
        text = grade ?: stringResource(R.string.academic_no_grade),
        modifier = Modifier
            .background(colors.first, MaterialTheme.shapes.large)
            .padding(
                horizontal = Dimension.Spacing.mediumLarge,
                vertical = Dimension.Spacing.small,
            ),
        color = colors.second,
        style = AppTextStyles.status,
    )
}

@Composable
private fun StatusMessage(
    message: String,
    isError: Boolean,
) {
    val background = if (isError) tseletrackRedTint else tseletrackOkTint
    val foreground = if (isError) tseletrackRed else tseletrackOk
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, MaterialTheme.shapes.medium)
            .padding(Dimension.Spacing.large),
    ) {
        Text(
            text = message,
            color = foreground,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
