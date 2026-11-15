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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
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
    progressPercent: Int,
    stages: List<RouteStage>,
    onSignContract: () -> Unit,
    onCareerGuidanceClick: () -> Unit,
    onUniversityChoiceClick: () -> Unit,
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
        AppScreenHeader(title = stringResource(R.string.route_title))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = 13.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.route_card_title),
                        color = tseletrackInk,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$progressPercent%",
                        color = tseletrackInk,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(12.dp))
                AppProgressBar(percent = progressPercent)
                Spacer(Modifier.height(6.dp))
                stages.forEachIndexed { index, stage ->
                    RouteStageItem(
                        stage = stage,
                        isFirst = index == 0,
                        isLast = index == stages.lastIndex,
                        onClick = when {
                            stage.isCareerGuidance -> onCareerGuidanceClick
                            stage.isUniversityChoice -> onUniversityChoiceClick
                            else -> null
                        },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(tseletrackRed, RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button, onClick = onSignContract),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.route_sign_contract),
                    color = tseletrackWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private val RouteStage.isCareerGuidance: Boolean
    get() = type == RouteStageType.CareerGuidance

private val RouteStage.isUniversityChoice: Boolean
    get() = type == RouteStageType.TargetOfferChoice

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
        Spacer(Modifier.width(8.dp))
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
            Spacer(Modifier.height(2.dp))
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
        modifier = Modifier.background(background, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            color = foreground,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
