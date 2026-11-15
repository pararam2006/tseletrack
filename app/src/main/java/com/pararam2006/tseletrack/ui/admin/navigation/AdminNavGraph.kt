package com.pararam2006.tseletrack.ui.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pararam2006.tseletrack.ui.admin.AdminDataScreen
import com.pararam2006.tseletrack.ui.admin.AdminOverviewScreen
import com.pararam2006.tseletrack.ui.admin.AdminUsersScreen
import com.pararam2006.tseletrack.ui.admin.AdminViewModel
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import com.pararam2006.tseletrack.ui.common.AppProfileScreen
import com.pararam2006.tseletrack.ui.common.ProfileRow
import com.pararam2006.tseletrack.ui.navigation.PageNavHost
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AdminNavGraph(
    navController: NavHostController,
    onUsersClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AdminViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PageNavHost(
        navController = navController,
        startDestination = AdminRoute.Overview,
        pageIndex = { it.adminPageIndex() },
        modifier = modifier,
    ) {
        composable<AdminRoute.Overview> {
            val dashboard = state.dashboard
            if (dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Обзор", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (dashboard == null) AppLoadingScreen("Обзор")
            else AdminOverviewScreen(
                dashboard = dashboard,
                errorMessage = state.errorMessage,
                isActionRunning = state.isActionRunning,
                onUsersClick = onUsersClick,
                onExportClick = viewModel::exportData,
            )
        }
        composable<AdminRoute.Users> {
            if (state.dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Пользователи", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (state.dashboard == null) AppLoadingScreen("Пользователи")
            else AdminUsersScreen(
                users = state.visibleUsers,
                searchQuery = state.searchQuery,
                errorMessage = state.errorMessage,
                isActionRunning = state.isActionRunning,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onAddUser = viewModel::addUser,
            )
        }
        composable<AdminRoute.Data> {
            val dashboard = state.dashboard
            if (dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Данные", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (dashboard == null) AppLoadingScreen("Данные")
            else AdminDataScreen(
                events = dashboard.events,
                errorMessage = state.errorMessage,
                isActionRunning = state.isActionRunning,
                onExportData = viewModel::exportData,
                onOpenRoles = viewModel::openRoles,
                onCreateBackup = viewModel::createBackup,
                onResetData = viewModel::resetData,
            )
        }
        composable<AdminRoute.Profile> {
            val profile = state.dashboard?.profile
            if (profile == null && state.errorMessage != null) {
                AppErrorScreen("Профиль", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (profile == null) {
                AppLoadingScreen("Профиль")
            } else {
                AppProfileScreen(
                    displayName = profile.displayName,
                    avatarLetter = profile.avatarLetter,
                    role = profile.role,
                    rows = listOf(
                        ProfileRow("Журнал действий", "Доступен"),
                    ),
                    onRowClick = viewModel::openProfileRow,
                    onLogout = onLogout,
                )
            }
        }
    }
}
