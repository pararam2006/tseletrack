package com.pararam2006.tseletrack.ui.university.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import com.pararam2006.tseletrack.ui.common.AppProfileScreen
import com.pararam2006.tseletrack.ui.common.EmploymentAnalyticsScreen
import com.pararam2006.tseletrack.ui.common.ProfileRow
import com.pararam2006.tseletrack.ui.university.UniversityQueueScreen
import com.pararam2006.tseletrack.ui.university.UniversityStudentsScreen
import com.pararam2006.tseletrack.ui.university.UniversityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UniversityNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<UniversityViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = UniversityRoute.Queue,
        modifier = modifier,
    ) {
        composable<UniversityRoute.Queue> {
            val dashboard = state.dashboard
            if (dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Очередь договоров", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (dashboard == null) {
                AppLoadingScreen("Очередь договоров")
            } else {
                UniversityQueueScreen(
                    dashboard = dashboard,
                    onConfirm = viewModel::confirmContract,
                    onDecline = viewModel::declineContract,
                )
            }
        }
        composable<UniversityRoute.Students> {
            if (state.dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Студенты", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (state.dashboard == null) {
                AppLoadingScreen("Студенты")
            } else {
                UniversityStudentsScreen(
                    students = state.visibleStudents,
                    searchQuery = state.searchQuery,
                    selectedStage = state.selectedStage,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onStageSelected = viewModel::selectStage,
                )
            }
        }
        composable<UniversityRoute.Analytics> {
            val analytics = state.dashboard?.analytics
            if (analytics == null && state.errorMessage != null) {
                AppErrorScreen("Аналитика", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (analytics == null) AppLoadingScreen("Аналитика")
            else EmploymentAnalyticsScreen(analytics)
        }
        composable<UniversityRoute.Profile> {
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
                        ProfileRow("Тема оформления", "Светлая"),
                        ProfileRow("Уведомления", "Включены"),
                        ProfileRow("Документы", "3 файла"),
                    ),
                    onRowClick = viewModel::openProfileRow,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                )
            }
        }
    }
}
