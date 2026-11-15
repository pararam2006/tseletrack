package com.pararam2006.tseletrack.ui.student.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pararam2006.tseletrack.ui.student.ProfileScreen
import com.pararam2006.tseletrack.ui.student.ProfileViewModel
import com.pararam2006.tseletrack.ui.student.RouteScreen
import com.pararam2006.tseletrack.ui.student.RouteViewModel
import com.pararam2006.tseletrack.ui.student.TasksScreen
import com.pararam2006.tseletrack.ui.student.TasksViewModel
import com.pararam2006.tseletrack.ui.student.TargetOffersScreen
import com.pararam2006.tseletrack.ui.student.TargetOffersViewModel
import com.pararam2006.tseletrack.ui.career.navigation.CareerGuidanceNavGraph
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun StudentNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routeViewModel = koinViewModel<RouteViewModel>()
    NavHost(
        navController = navController,
        startDestination = StudentRoute.Roadmap,
        modifier = modifier,
    ) {
        composable<StudentRoute.Roadmap> {
            val state by routeViewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.route_title), error, routeViewModel::retry)
            } else {
                RouteScreen(
                    isLoading = state.isLoading,
                    progressPercent = state.progressPercent,
                    stages = state.stages,
                    onSignContract = routeViewModel::signContract,
                    onCareerGuidanceClick = {
                        navController.navigate(StudentRoute.CareerGuidance)
                    },
                    onUniversityChoiceClick = {
                        navController.navigateToStudentRoute(StudentRoute.TargetOffers)
                    },
                )
            }
        }

        composable<StudentRoute.CareerGuidance> {
            CareerGuidanceNavGraph(
                isAuthenticated = true,
                onBack = navController::popBackStack,
                onRequestAuth = {},
                onFinished = {
                    routeViewModel.refresh()
                    navController.popBackStack()
                },
            )
        }

        composable<StudentRoute.TargetOffers> {
            val viewModel = koinViewModel<TargetOffersViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(state.admissionStatus?.checkedAt) {
                if (state.admissionStatus?.checkedAt != null) routeViewModel.refresh()
            }
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.target_offers_title), error, viewModel::retry)
            } else {
                TargetOffersScreen(
                    state = state,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onRefreshCatalog = viewModel::refreshCatalog,
                    onSaveOfferToRoute = viewModel::saveOfferToRoute,
                    onShowCatalog = viewModel::showCatalog,
                    onApplicantCodeChange = viewModel::updateApplicantCode,
                    onSaveApplicantCode = viewModel::saveApplicantCode,
                    onRefreshAdmissionStatus = viewModel::refreshAdmissionStatus,
                )
            }
        }

        composable<StudentRoute.Tasks> {
            val viewModel = koinViewModel<TasksViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.tasks_title), error, viewModel::retry)
            } else {
                TasksScreen(
                    isLoading = state.isLoading,
                    tasks = state.tasks,
                    onTaskClick = viewModel::toggleTask,
                    onAddTask = viewModel::addTask,
                )
            }
        }

        composable<StudentRoute.Profile> {
            val viewModel = koinViewModel<ProfileViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.profile_title), error, viewModel::retry)
            } else {
                ProfileScreen(
                    isLoading = state.isLoading,
                    profile = state.profile,
                    onSettingClick = viewModel::openSetting,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    },
                )
            }
        }
    }
}
