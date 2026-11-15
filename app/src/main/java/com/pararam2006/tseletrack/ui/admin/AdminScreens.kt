package com.pararam2006.tseletrack.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.domain.role.AdminDashboard
import com.pararam2006.tseletrack.domain.role.AdminUser
import com.pararam2006.tseletrack.domain.role.SystemEvent
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppFloatingButton
import com.pararam2006.tseletrack.ui.common.AppInlineNotice
import com.pararam2006.tseletrack.ui.common.AppMetricCard
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreen
import com.pararam2006.tseletrack.ui.common.AppSearchField
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint

@Composable
internal fun AdminOverviewScreen(
    dashboard: AdminDashboard,
    errorMessage: String?,
    isActionRunning: Boolean,
    onUsersClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    AppScreen(title = "Обзор") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            errorMessage?.let { message ->
                AppInlineNotice(message = message, isError = true)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
            ) {
                AppMetricCard(
                    value = dashboard.userCount.toString(),
                    label = "пользователей",
                    modifier = Modifier.weight(1f),
                )
                AppMetricCard(
                    value = dashboard.studentCount.toString(),
                    label = "студентов",
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
            ) {
                AppMetricCard(
                    value = dashboard.contractsWaiting.toString(),
                    label = "ждут договор",
                    valueColor = tseletrackWarn,
                    modifier = Modifier.weight(1f),
                )
                AppMetricCard(
                    value = "${dashboard.employedPercent}%",
                    label = "трудоустроены",
                    valueColor = tseletrackOk,
                    modifier = Modifier.weight(1f),
                )
            }
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                dashboard.stages.forEach { stage ->
                    Text(
                        text = stage.title,
                        color = tseletrackInk,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(Dimension.Spacing.xs))
                    AppProgressBar(percent = stage.percent, height = Dimension.Size.progressCompact)
                    Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
                }
            }
            AppPrimaryButton(text = "Пользователи", onClick = onUsersClick)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .clickable(
                        enabled = !isActionRunning,
                        role = Role.Button,
                        onClick = onExportClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Экспорт данных",
                    color = tseletrackInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun AdminUsersScreen(
    users: List<AdminUser>,
    searchQuery: String,
    errorMessage: String?,
    isActionRunning: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onAddUser: () -> Unit,
) {
    AppScreen(title = "Пользователи") {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimension.Padding.screen),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.space9),
            ) {
                item {
                    AppSearchField(
                        value = searchQuery,
                        placeholder = "Поиск пользователя",
                        onValueChange = onSearchQueryChange,
                    )
                }
                errorMessage?.let { message ->
                    item { AppInlineNotice(message = message, isError = true) }
                }
                items(users, key = AdminUser::id) { user ->
                    AdminUserCard(user)
                }
                item { Spacer(Modifier.height(Dimension.Size.header)) }
            }
            AppFloatingButton(
                contentDescription = "Добавить пользователя",
                onClick = onAddUser,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Dimension.Padding.screen),
                enabled = !isActionRunning,
            )
        }
    }
}

@Composable
private fun AdminUserCard(user: AdminUser) {
    val (background, foreground) = roleColors(user.role)
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = Dimension.Spacing.large,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(background, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.avatarLetter,
                    color = foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimension.Spacing.large),
            ) {
                Text(
                    text = user.name,
                    color = tseletrackInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = user.email,
                    color = tseletrackMut,
                    fontSize = 10.sp,
                )
            }
            Text(
                text = user.role,
                modifier = Modifier
                    .background(background, MaterialTheme.shapes.large)
                    .padding(horizontal = Dimension.Spacing.space9, vertical = Dimension.Spacing.space3),
                color = foreground,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun AdminDataScreen(
    events: List<SystemEvent>,
    errorMessage: String?,
    isActionRunning: Boolean,
    onExportData: () -> Unit,
    onOpenRoles: () -> Unit,
    onCreateBackup: () -> Unit,
    onResetData: () -> Unit,
) {
    AppScreen(title = "Данные") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimension.Padding.screen),
        ) {
            errorMessage?.let { message ->
                AppInlineNotice(message = message, isError = true)
                Spacer(Modifier.height(Dimension.Spacing.space9))
            }
            DataActionRow("Экспорт данных (JSON)", !isActionRunning, onExportData)
            Spacer(Modifier.height(Dimension.Spacing.space9))
            DataActionRow("Роли и права", !isActionRunning, onOpenRoles)
            Spacer(Modifier.height(Dimension.Spacing.space9))
            DataActionRow("Резервная копия", !isActionRunning, onCreateBackup)
            Spacer(Modifier.height(Dimension.Layout.adminActionsBottom))
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Журнал событий",
                    color = tseletrackInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Dimension.Spacing.large))
                events.forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimension.Spacing.small),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = event.title,
                            color = tseletrackMut,
                            fontSize = 11.sp,
                        )
                        Text(
                            text = event.date,
                            color = tseletrackMut,
                            fontSize = 10.sp,
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            AppPrimaryButton(
                text = "Сбросить все данные",
                onClick = onResetData,
                enabled = !isActionRunning,
            )
        }
    }
}

@Composable
private fun DataActionRow(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = Dimension.Padding.card,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = if (enabled) tseletrackInk else tseletrackMut,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = "›", color = tseletrackMut, fontSize = 20.sp)
        }
    }
}

private fun roleColors(role: String): Pair<Color, Color> = when (role) {
    "Вуз" -> tseletrackOkTint to tseletrackOk
    "Работодатель" -> tseletrackWarnTint to tseletrackWarn
    "Админ" -> tseletrackRedTint to tseletrackRed
    else -> tseletrackInfoTint to tseletrackInfo
}
