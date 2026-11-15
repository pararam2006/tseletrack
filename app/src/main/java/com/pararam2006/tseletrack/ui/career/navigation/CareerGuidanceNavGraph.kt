package com.pararam2006.tseletrack.ui.career.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pararam2006.tseletrack.ui.career.CareerAuthGateScreen
import com.pararam2006.tseletrack.ui.career.CareerGuidanceLandingScreen
import com.pararam2006.tseletrack.ui.career.CareerGuidanceViewModel
import com.pararam2006.tseletrack.ui.career.CareerQuestionScreen
import com.pararam2006.tseletrack.ui.career.CareerResultScreen
import com.pararam2006.tseletrack.ui.career.CareerSyncedScreen
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.navigation.PageNavHost
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

private sealed interface CareerGuidanceRoute {
    @Serializable
    data object Landing : CareerGuidanceRoute

    @Serializable
    data object Question : CareerGuidanceRoute

    @Serializable
    data object Result : CareerGuidanceRoute

    @Serializable
    data object AuthGate : CareerGuidanceRoute

    @Serializable
    data object Synced : CareerGuidanceRoute
}

@Composable
fun CareerGuidanceNavGraph(
    isAuthenticated: Boolean,
    onBack: () -> Unit,
    onRequestAuth: () -> Unit,
    onFinished: () -> Unit,
) {
    val viewModel = koinViewModel<CareerGuidanceViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) {
        AppLoadingScreen(title = "Профориентация")
        return
    }

    val navController = rememberNavController()
    PageNavHost(
        navController = navController,
        startDestination = CareerGuidanceRoute.Landing,
        pageIndex = { it.careerGuidancePageIndex() },
    ) {
        composable<CareerGuidanceRoute.Landing> {
            LaunchedEffect(state.result, state.isSynced, isAuthenticated) {
                if (state.result != null) {
                    navController.navigate(
                        if (state.isSynced && isAuthenticated) {
                            CareerGuidanceRoute.Synced
                        } else {
                            CareerGuidanceRoute.Result
                        },
                    )
                }
            }
            CareerGuidanceLandingScreen(
                state = state,
                onBack = onBack,
                onStart = {
                    viewModel.startNew()
                    navController.navigate(CareerGuidanceRoute.Question)
                },
                onResume = { navController.navigate(CareerGuidanceRoute.Question) },
                onRestart = {
                    viewModel.startNew()
                    navController.navigate(CareerGuidanceRoute.Question)
                },
            )
        }

        composable<CareerGuidanceRoute.Question> {
            LaunchedEffect(state.result) {
                if (state.result != null) {
                    navController.navigate(CareerGuidanceRoute.Result)
                }
            }
            CareerQuestionScreen(
                state = state,
                onBack = {
                    if (!viewModel.previousQuestion()) {
                        navController.popBackStack()
                    }
                },
                onOptionSelected = viewModel::selectOption,
                onNext = viewModel::nextQuestion,
            )
        }

        composable<CareerGuidanceRoute.Result> {
            val result = state.result
            if (result == null) {
                LaunchedEffect(Unit) { navController.returnToLanding() }
                return@composable
            }
            var syncRequested by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(state.isSynced, syncRequested) {
                if (state.isSynced && syncRequested) {
                    navController.navigate(CareerGuidanceRoute.Synced)
                }
            }
            CareerResultScreen(
                result = result,
                isAuthenticated = isAuthenticated,
                isSyncing = state.isSyncing,
                syncError = state.syncError,
                onBack = { navController.popBackStackOr(onBack) },
                onSave = {
                    if (isAuthenticated) {
                        syncRequested = true
                        viewModel.syncResult()
                    } else {
                        navController.navigate(CareerGuidanceRoute.AuthGate)
                    }
                },
                onRestart = {
                    viewModel.startNew()
                    navController.returnToQuestion()
                },
            )
        }

        composable<CareerGuidanceRoute.AuthGate> {
            CareerAuthGateScreen(
                onBack = navController::popBackStack,
                onLogin = onRequestAuth,
                onStayGuest = navController::popBackStack,
            )
        }

        composable<CareerGuidanceRoute.Synced> {
            val result = state.result
            if (result == null) {
                LaunchedEffect(Unit) { navController.returnToLanding() }
                return@composable
            }
            CareerSyncedScreen(
                result = result,
                onBack = onFinished,
                onRoute = onFinished,
                onRecommendations = { navController.navigate(CareerGuidanceRoute.Result) },
            )
        }
    }
}

private fun NavDestination.careerGuidancePageIndex(): Int? = when {
    hasRoute<CareerGuidanceRoute.Landing>() -> 0
    hasRoute<CareerGuidanceRoute.Question>() -> 1
    hasRoute<CareerGuidanceRoute.Result>() -> 2
    hasRoute<CareerGuidanceRoute.AuthGate>() -> 3
    hasRoute<CareerGuidanceRoute.Synced>() -> 3
    else -> null
}

private fun NavHostController.returnToLanding() {
    navigate(CareerGuidanceRoute.Landing) {
        popUpTo(graph.id) { inclusive = true }
    }
}

private fun NavHostController.returnToQuestion() {
    navigate(CareerGuidanceRoute.Question) {
        popUpTo(graph.id) { inclusive = true }
    }
}

private fun NavHostController.popBackStackOr(fallback: () -> Unit) {
    if (!popBackStack()) fallback()
}
