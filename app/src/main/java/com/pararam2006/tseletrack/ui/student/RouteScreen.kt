package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreenHeader
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

@Composable
internal fun RouteScreen(
    isLoading: Boolean,
    contractStatus: StudentDocumentStatus,
    hasSelectedOffer: Boolean,
    admissionState: AdmissionState?,
    progressPercent: Int,
    stages: List<RouteStage>,
    onCareerGuidanceClick: () -> Unit,
    onUniversityChoiceClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onAcademicPerformanceClick: () -> Unit,
) {
    if (isLoading) {
        AppLoadingScreen(title = stringResource(R.string.route_title))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        val primaryAction = stages.primaryAction(
            contractStatus = contractStatus,
            hasSelectedOffer = hasSelectedOffer,
            admissionState = admissionState,
        )
        AppScreenHeader(title = stringResource(R.string.route_title))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimension.Padding.screen),
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = Dimension.Spacing.space13,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.route_card_title),
                        color = tseletrackInk,
                        style = AppTextStyles.sectionTitle,
                    )
                    Text(
                        text = "$progressPercent%",
                        color = tseletrackInk,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(Dimension.Spacing.large))
                AppProgressBar(percent = progressPercent)
                Spacer(Modifier.height(Dimension.Spacing.small))
                stages.forEachIndexed { index, stage ->
                    RouteStageItem(
                        stage = stage,
                        isFirst = index == 0,
                        isLast = index == stages.lastIndex,
                        onClick = when {
                            stage.isCareerGuidance -> onCareerGuidanceClick
                            stage.isUniversityChoice -> onUniversityChoiceClick
                            stage.isAcademicPerformanceAvailable -> onAcademicPerformanceClick
                            else -> null
                        },
                    )
                }
            }
            Spacer(Modifier.height(Dimension.Spacing.large))
            RoutePrimaryButton(
                action = primaryAction,
                onClick = when (primaryAction) {
                    RoutePrimaryAction.CareerGuidance -> onCareerGuidanceClick
                    RoutePrimaryAction.ChooseTargetOffer,
                    RoutePrimaryAction.SubmitDocuments,
                    RoutePrimaryAction.CheckApplication,
                    RoutePrimaryAction.TrackApplication,
                    RoutePrimaryAction.CheckEnrollmentOrder,
                    RoutePrimaryAction.EnrollmentConfirmed,
                    -> onUniversityChoiceClick
                    RoutePrimaryAction.UploadContract,
                    RoutePrimaryAction.FixContract,
                    RoutePrimaryAction.EmploymentDocuments,
                    RoutePrimaryAction.CurrentStageDocuments,
                    -> onDocumentsClick

                    RoutePrimaryAction.AcademicPerformance -> onAcademicPerformanceClick

                    RoutePrimaryAction.ContractLocked,
                    RoutePrimaryAction.ContractPending,
                    RoutePrimaryAction.ContractAccepted,
                    RoutePrimaryAction.Waiting,
                    RoutePrimaryAction.Completed,
                    -> ({})
                },
            )
        }
    }
}

@Composable
private fun RoutePrimaryButton(
    action: RoutePrimaryAction,
    onClick: () -> Unit,
) {
    val enabled = action.isEnabled
    val background = when {
        action == RoutePrimaryAction.Completed -> tseletrackOk
        enabled -> tseletrackRed
        else -> tseletrackLine
    }
    val contentColor = if (enabled || action == RoutePrimaryAction.Completed) {
        tseletrackWhite
    } else {
        tseletrackMut
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimension.Size.button)
            .background(background, MaterialTheme.shapes.large)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = action.label(),
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
    }
}

@Composable
private fun RoutePrimaryAction.label(): String = stringResource(
    when (this) {
        RoutePrimaryAction.CareerGuidance -> R.string.route_action_career_guidance
        RoutePrimaryAction.ChooseTargetOffer -> R.string.route_action_choose_target_offer
        RoutePrimaryAction.SubmitDocuments -> R.string.route_action_submit_documents
        RoutePrimaryAction.CheckApplication -> R.string.route_action_check_application
        RoutePrimaryAction.TrackApplication -> R.string.route_action_track_application
        RoutePrimaryAction.CheckEnrollmentOrder -> R.string.route_action_check_enrollment
        RoutePrimaryAction.EnrollmentConfirmed -> R.string.route_action_enrollment_confirmed
        RoutePrimaryAction.UploadContract -> R.string.route_action_upload_contract
        RoutePrimaryAction.FixContract -> R.string.route_action_fix_contract
        RoutePrimaryAction.ContractLocked -> R.string.route_action_contract_locked
        RoutePrimaryAction.ContractPending -> R.string.route_action_contract_pending
        RoutePrimaryAction.ContractAccepted -> R.string.route_action_contract_accepted
        RoutePrimaryAction.AcademicPerformance -> R.string.route_action_education
        RoutePrimaryAction.EmploymentDocuments -> R.string.route_action_employment
        RoutePrimaryAction.CurrentStageDocuments -> R.string.route_action_current_stage
        RoutePrimaryAction.Waiting -> R.string.route_action_waiting
        RoutePrimaryAction.Completed -> R.string.route_action_completed
    },
)

private val RouteStage.isCareerGuidance: Boolean
    get() = type == RouteStageType.CareerGuidance

private val RouteStage.isUniversityChoice: Boolean
    get() = type == RouteStageType.TargetOfferChoice

private val RouteStage.isAcademicPerformanceAvailable: Boolean
    get() = type == RouteStageType.Education && status in setOf(
        RouteStageStatus.Current,
        RouteStageStatus.Completed,
    )

@Composable
private fun RouteStageItem(
    stage: RouteStage,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RouteStageMarker(
            status = stage.status,
            drawTopLine = !isFirst,
            drawBottomLine = !isLast,
        )
        Spacer(Modifier.width(Dimension.Spacing.medium))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stage.title,
                color = tseletrackInk,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            Spacer(Modifier.height(Dimension.Spacing.xxs))
            RouteStageStatusPill(status = stage.status)
        }
        if (onClick != null) {
            Text(
                text = stringResource(R.string.route_open_stage),
                color = tseletrackRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun RouteStageMarker(
    status: RouteStageStatus,
    drawTopLine: Boolean,
    drawBottomLine: Boolean,
) {
    val completedColor = tseletrackOk
    val currentColor = tseletrackRed
    val pendingColor = tseletrackLine
    Canvas(
        modifier = Modifier.size(width = 28.dp, height = 44.dp),
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = 8.dp.toPx()
        val lineWidth = 2.dp.toPx()
        if (drawTopLine) {
            drawLine(
                color = pendingColor,
                start = Offset(center.x, 0f),
                end = Offset(center.x, center.y - radius),
                strokeWidth = lineWidth,
            )
        }
        if (drawBottomLine) {
            drawLine(
                color = pendingColor,
                start = Offset(center.x, center.y + radius),
                end = Offset(center.x, size.height),
                strokeWidth = lineWidth,
            )
        }

        when (status) {
            RouteStageStatus.Completed -> {
                drawCircle(color = completedColor, radius = radius, center = center)
                drawLine(
                    color = Color.White,
                    start = Offset(center.x - 5.dp.toPx(), center.y),
                    end = Offset(center.x - 1.dp.toPx(), center.y + 4.dp.toPx()),
                    strokeWidth = 2.dp.toPx(),
                )
                drawLine(
                    color = Color.White,
                    start = Offset(center.x - 1.dp.toPx(), center.y + 4.dp.toPx()),
                    end = Offset(center.x + 6.dp.toPx(), center.y - 5.dp.toPx()),
                    strokeWidth = 2.dp.toPx(),
                )
            }

            RouteStageStatus.Current -> drawCircle(
                color = currentColor,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx()),
            )

            RouteStageStatus.Waiting,
            RouteStageStatus.Future,
            -> drawCircle(color = pendingColor, radius = radius, center = center)
        }
    }
}

@Composable
private fun RouteStageStatusPill(status: RouteStageStatus) {
    val (label, background, foreground) = when (status) {
        RouteStageStatus.Completed -> Triple(
            stringResource(R.string.route_status_completed),
            tseletrackOkTint,
            tseletrackOk,
        )

        RouteStageStatus.Current -> Triple(
            stringResource(R.string.route_status_current),
            tseletrackInfoTint,
            tseletrackInfo,
        )

        RouteStageStatus.Waiting -> Triple(
            stringResource(R.string.route_status_waiting),
            tseletrackLine,
            tseletrackMut,
        )

        RouteStageStatus.Future -> Triple(
            stringResource(R.string.route_status_future),
            tseletrackLine,
            tseletrackMut,
        )
    }
    Box(
        modifier = Modifier.background(background, AppShapes.pill),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = Dimension.Spacing.mediumLarge, vertical = Dimension.Spacing.xxs),
            color = foreground,
            style = AppTextStyles.status,
        )
    }
}
