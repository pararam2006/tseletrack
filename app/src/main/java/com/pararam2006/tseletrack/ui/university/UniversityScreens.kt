package com.pararam2006.tseletrack.ui.university

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.domain.role.ContractQueueItem
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.role.UniversityStudentItem
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppFilterChip
import com.pararam2006.tseletrack.ui.common.AppMetricCard
import com.pararam2006.tseletrack.ui.common.AppOutlinedButton
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreen
import com.pararam2006.tseletrack.ui.common.AppSearchField
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint

@Composable
internal fun UniversityQueueScreen(
    dashboard: UniversityDashboard,
    onConfirm: (String) -> Unit,
    onDecline: (String) -> Unit,
    onOpenDocument: (String) -> Unit,
) {
    AppScreen(title = "Очередь договоров") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            item {
                Text(
                    text = "ТулГУ",
                    modifier = Modifier
                        .background(
                            color = com.pararam2006.tseletrack.ui.theme.tseletrackOkTint,
                            shape = MaterialTheme.shapes.large,
                        )
                        .padding(horizontal = Dimension.Spacing.mediumLarge, vertical = Dimension.Spacing.space3),
                    color = com.pararam2006.tseletrack.ui.theme.tseletrackOk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
                ) {
                    AppMetricCard(
                        value = dashboard.studentCount.toString(),
                        label = "студентов",
                        modifier = Modifier.weight(1f),
                    )
                    AppMetricCard(
                        value = dashboard.queueCount.toString(),
                        label = "в очереди",
                        valueColor = tseletrackWarn,
                        modifier = Modifier.weight(1f),
                    )
                    AppMetricCard(
                        value = dashboard.availablePlaces.toString(),
                        label = "мест",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            items(dashboard.queue, key = ContractQueueItem::id) { contract ->
                ContractQueueCard(
                    item = contract,
                    onConfirm = { onConfirm(contract.id) },
                    onDecline = { onDecline(contract.id) },
                    onOpenDocument = { onOpenDocument(contract.id) },
                )
            }
        }
    }
}

@Composable
private fun ContractQueueCard(
    item: ContractQueueItem,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    onOpenDocument: () -> Unit,
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
                    text = item.avatarLetter,
                    color = tseletrackRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(modifier = Modifier.padding(start = Dimension.Spacing.large)) {
                Text(
                    text = item.studentName,
                    color = tseletrackInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.signedAt,
                    color = tseletrackMut,
                    fontSize = 11.sp,
                )
                item.fileName?.let { fileName ->
                    Text(
                        text = fileName,
                        color = tseletrackMut,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }
        }
        Spacer(Modifier.height(Dimension.Spacing.medium))
        Text(
            text = "Ждёт подтверждения",
            modifier = Modifier
                .background(tseletrackWarnTint, MaterialTheme.shapes.large)
                .padding(horizontal = Dimension.Spacing.space9, vertical = Dimension.Spacing.space3),
            color = tseletrackWarn,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(Dimension.Spacing.medium))
        if (item.documentId != null && item.fileName != null) {
            AppOutlinedButton(
                text = "Открыть PDF",
                onClick = onOpenDocument,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Dimension.Spacing.medium))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge)) {
            AppPrimaryButton(
                text = "Подтвердить договор",
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
            )
            AppOutlinedButton(
                text = "На доработку",
                onClick = onDecline,
                modifier = Modifier.height(Dimension.Size.button),
            )
        }
    }
}

@Composable
internal fun UniversityStudentsScreen(
    students: List<UniversityStudentItem>,
    searchQuery: String,
    selectedStage: String,
    onSearchQueryChange: (String) -> Unit,
    onStageSelected: (String) -> Unit,
) {
    val stages = listOf("Все", "Договор", "Обучение", "Работа")
    AppScreen(title = "Студенты") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            item {
                AppSearchField(
                    value = searchQuery,
                    placeholder = "Поиск студента",
                    onValueChange = onSearchQueryChange,
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.small),
                ) {
                    stages.forEach { stage ->
                        AppFilterChip(
                            text = stage,
                            isSelected = stage == selectedStage,
                            onClick = { onStageSelected(stage) },
                            modifier = if (stage == "Все") Modifier else Modifier.weight(1f),
                        )
                    }
                }
            }
            items(students, key = UniversityStudentItem::id) { student ->
                StudentProgressCard(student)
            }
        }
    }
}

@Composable
private fun StudentProgressCard(student: UniversityStudentItem) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = Dimension.Spacing.space13,
    ) {
        Text(
            text = student.name,
            color = tseletrackInk,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(Dimension.Spacing.space5))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.largeExtra),
        ) {
            Text(
                text = student.stage,
                modifier = Modifier
                    .background(tseletrackInfoTint, MaterialTheme.shapes.large)
                    .padding(horizontal = Dimension.Spacing.space9, vertical = Dimension.Spacing.space3),
                color = tseletrackInfo,
                fontSize = 10.sp,
            )
            AppProgressBar(
                percent = student.progressPercent,
                modifier = Modifier.weight(1f),
                height = Dimension.Size.progressCompact,
            )
        }
    }
}
