package com.pararam2006.tseletrack.ui.university.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import com.pararam2006.tseletrack.ui.common.AppProfileScreen
import com.pararam2006.tseletrack.ui.common.ProfileRow
import com.pararam2006.tseletrack.ui.university.UniversityQueueScreen
import com.pararam2006.tseletrack.ui.university.UniversityStudentsScreen
import com.pararam2006.tseletrack.ui.university.UniversityAdmissionAnalyticsScreen
import com.pararam2006.tseletrack.ui.university.UniversityViewModel
import com.pararam2006.tseletrack.ui.common.openDownloadedDocument
import com.pararam2006.tseletrack.ui.navigation.PageNavHost
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UniversityNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<UniversityViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.documentsToOpen.collect { document ->
            if (!context.openDownloadedDocument(document)) {
                viewModel.documentOpenFailed()
            }
        }
    }

    PageNavHost(
        navController = navController,
        startDestination = UniversityRoute.Analytics,
        pageIndex = { it.universityPageIndex() },
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
                    onOpenDocument = viewModel::openContractDocument,
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
            val dashboard = state.dashboard
            if (dashboard == null && state.errorMessage != null) {
                AppErrorScreen("Аналитика", state.errorMessage.orEmpty(), viewModel::retry)
            } else if (dashboard == null) {
                AppLoadingScreen("Аналитика")
            } else {
                UniversityAdmissionAnalyticsScreen(
                    metrics = dashboard.targetOfferMetrics,
                    visibleMetrics = state.visibleTargetOfferMetrics,
                    selectedFilter = state.selectedOfferDemandFilter,
                    onFilterSelected = viewModel::selectOfferDemandFilter,
                )
            }
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
                        ProfileRow("Документы", "3 файла"),
                    ),
                    onRowClick = viewModel::openProfileRow,
                    onLogout = onLogout,
                )
            }
        }
    }
}
