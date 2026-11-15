package com.pararam2006.tseletrack.ui.company.navigation

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
import com.pararam2006.tseletrack.ui.company.CompanyCandidatesScreen
import com.pararam2006.tseletrack.ui.company.CompanyViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CompanyNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<CompanyViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = CompanyRoute.Candidates,
        modifier = modifier,
    ) {
        composable<CompanyRoute.Candidates> {
            if (state.dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Кандидаты", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (state.dashboard == null) {
                AppLoadingScreen("Кандидаты")
            } else {
                CompanyCandidatesScreen(
                    candidates = state.visibleCandidates,
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = viewModel::selectFilter,
                    onAdvance = viewModel::advanceCandidate,
                    onDecline = viewModel::declineCandidate,
                )
            }
        }
        composable<CompanyRoute.Analytics> {
            val analytics = state.dashboard?.analytics
            if (analytics == null && state.errorMessage != null) {
                AppErrorScreen("Аналитика", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (analytics == null) AppLoadingScreen("Аналитика")
            else EmploymentAnalyticsScreen(analytics)
        }
        composable<CompanyRoute.Profile> {
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
                        ProfileRow("Документы", "5 файлов"),
                    ),
                    onRowClick = viewModel::openProfileRow,
                    onLogout = onLogout,
                )
            }
        }
    }
}
