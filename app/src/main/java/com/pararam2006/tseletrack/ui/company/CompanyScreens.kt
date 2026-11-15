package com.pararam2006.tseletrack.ui.company

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.domain.role.CandidateItem
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppFilterChip
import com.pararam2006.tseletrack.ui.common.AppOutlinedButton
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreen
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint

@Composable
internal fun CompanyCandidatesScreen(
    candidates: List<CandidateItem>,
    selectedFilter: CandidateFilter,
    onFilterSelected: (CandidateFilter) -> Unit,
    onAdvance: (String, CandidateStatus) -> Unit,
    onDecline: (String) -> Unit,
) {
    AppScreen(title = "Кандидаты") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.small),
                ) {
                    CandidateFilter.entries.forEach { filter ->
                        AppFilterChip(
                            text = filter.title,
                            isSelected = selectedFilter == filter,
                            onClick = { onFilterSelected(filter) },
                            modifier = if (filter == CandidateFilter.All) Modifier else Modifier.weight(1f),
                        )
                    }
                }
            }
            items(candidates, key = CandidateItem::id) { candidate ->
                CandidateCard(
                    candidate = candidate,
                    onAdvance = { onAdvance(candidate.id, candidate.nextStatus()) },
                    onDecline = { onDecline(candidate.id) },
                )
            }
        }
    }
}

@Composable
private fun CandidateCard(
    candidate: CandidateItem,
    onAdvance: () -> Unit,
    onDecline: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(tseletrackRedTint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = candidate.avatarLetter,
                    color = tseletrackRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(modifier = Modifier.padding(start = Dimension.Spacing.large)) {
                Text(
                    text = candidate.name,
                    color = tseletrackInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${candidate.university} · ${candidate.stage}",
                    color = tseletrackMut,
                    fontSize = 11.sp,
                )
            }
        }
        Spacer(Modifier.height(Dimension.Spacing.large))
        AppProgressBar(percent = candidate.progressPercent)
        Spacer(Modifier.height(Dimension.Spacing.space7))
        Text(
            text = "Прогресс ${candidate.progressPercent}%",
            color = tseletrackMut,
            fontSize = 10.sp,
        )
        Spacer(Modifier.height(Dimension.Spacing.space9))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge)) {
            if (candidate.status == CandidateStatus.Completed) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .background(tseletrackOkTint, AppShapes.outlinedButton),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Маршрут завершён",
                        color = tseletrackOk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                AppPrimaryButton(
                    text = candidate.actionTitle(),
                    onClick = onAdvance,
                    modifier = Modifier.weight(1f),
                )
            }
            AppOutlinedButton(text = "Отзыв", onClick = onDecline)
        }
    }
}

private fun CandidateItem.nextStatus(): CandidateStatus = when (status) {
    CandidateStatus.Education -> CandidateStatus.Employment
    CandidateStatus.Employment -> CandidateStatus.Retention
    CandidateStatus.Retention -> CandidateStatus.Completed
    CandidateStatus.Completed -> CandidateStatus.Completed
}

private fun CandidateItem.actionTitle(): String = when (status) {
    CandidateStatus.Education -> "Трудоустроен"
    CandidateStatus.Employment -> "Удержан"
    CandidateStatus.Retention -> "Завершить маршрут"
    CandidateStatus.Completed -> "Маршрут завершён"
}
