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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.domain.role.AdminDashboard
import com.pararam2006.tseletrack.domain.role.AdminUser
import com.pararam2006.tseletrack.domain.role.SystemEvent
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppFloatingButton
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
    onUsersClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    AppScreen(title = "Обзор") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                    Spacer(Modifier.height(4.dp))
                    AppProgressBar(percent = stage.percent, height = 5.dp)
                    Spacer(Modifier.height(10.dp))
                }
            }
            AppPrimaryButton(text = "Пользователи", onClick = onUsersClick)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .clickable(role = Role.Button, onClick = onExportClick),
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
    onSearchQueryChange: (String) -> Unit,
    onAddUser: () -> Unit,
) {
    AppScreen(title = "Пользователи") {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item {
                    AppSearchField(
                        value = searchQuery,
                        placeholder = "Поиск пользователя",
                        onValueChange = onSearchQueryChange,
                    )
                }
                items(users, key = AdminUser::id) { user ->
                    AdminUserCard(user)
                }
                item { Spacer(Modifier.height(56.dp)) }
            }
            AppFloatingButton(
                contentDescription = "Добавить пользователя",
                onClick = onAddUser,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun AdminUserCard(user: AdminUser) {
    val (background, foreground) = roleColors(user.role)
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 12.dp,
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
                    .padding(horizontal = 12.dp),
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
                    .background(background, RoundedCornerShape(12.dp))
                    .padding(horizontal = 9.dp, vertical = 3.dp),
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
    onExportData: () -> Unit,
    onOpenRoles: () -> Unit,
    onCreateBackup: () -> Unit,
    onResetData: () -> Unit,
) {
    AppScreen(title = "Данные") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            DataActionRow("Экспорт данных (JSON)", onExportData)
            Spacer(Modifier.height(9.dp))
            DataActionRow("Роли и права", onOpenRoles)
            Spacer(Modifier.height(9.dp))
            DataActionRow("Резервная копия", onCreateBackup)
            Spacer(Modifier.height(110.dp))
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Журнал событий",
                    color = tseletrackInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(12.dp))
                events.forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
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
            AppPrimaryButton(text = "Сбросить все данные", onClick = onResetData)
        }
    }
}

@Composable
private fun DataActionRow(
    title: String,
    onClick: () -> Unit,
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(role = Role.Button, onClick = onClick),
        contentPadding = 14.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = tseletrackInk,
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
