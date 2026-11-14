package com.pararam2006.tseletrack.ui.student.navigation

import androidx.compose.runtime.Composable
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
import com.pararam2006.tseletrack.ui.student.UniversitiesScreen
import com.pararam2006.tseletrack.ui.student.UniversitiesViewModel
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun StudentNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = StudentRoute.Roadmap,
        modifier = modifier,
    ) {
        composable<StudentRoute.Roadmap> {
            val viewModel = koinViewModel<RouteViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.route_title), error, viewModel::retry)
            } else {
                RouteScreen(
                    isLoading = state.isLoading,
                    progressPercent = state.progressPercent,
                    stages = state.stages,
                    onSignContract = viewModel::signContract,
                )
            }
        }

        composable<StudentRoute.Universities> {
            val viewModel = koinViewModel<UniversitiesViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.universities_title), error, viewModel::retry)
            } else {
                UniversitiesScreen(
                    isLoading = state.isLoading,
                    universities = state.visibleUniversities,
                    searchQuery = state.searchQuery,
                    selectedField = state.selectedField,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onFieldSelected = viewModel::selectStudyField,
                    onChooseUniversity = viewModel::chooseUniversity,
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
