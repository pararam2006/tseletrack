package com.pararam2006.tseletrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.core.Route
import com.pararam2006.tseletrack.ui.admin.AdminRoot
import com.pararam2006.tseletrack.ui.company.CompanyRoot
import com.pararam2006.tseletrack.ui.career.navigation.CareerGuidanceNavGraph
import com.pararam2006.tseletrack.ui.login.LoginScreen
import com.pararam2006.tseletrack.ui.registration.RegistrationScreen
import com.pararam2006.tseletrack.ui.student.StudentRoot
import com.pararam2006.tseletrack.ui.university.UniversityRoot
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.navigation.PageNavHost
import com.pararam2006.tseletrack.ui.session.SessionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavGraph() {
    val sessionViewModel = koinViewModel<SessionViewModel>()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    if (sessionState.isChecking) {
        AppLoadingScreen(title = "Tseletrack")
        return
    }

    val navController = rememberNavController()
    LaunchedEffect(sessionViewModel, navController) {
        sessionViewModel.logoutCompleted.collect {
            navController.navigateToLogin()
        }
    }
    val startDestination = sessionState.role?.toRoute() ?: Route.Login
    PageNavHost(
        navController = navController,
        startDestination = startDestination,
        pageIndex = { it.mainPageIndex() },
    ) {
        composable<Route.Login> {
            LoginScreen(
                onLoginSuccess = { role: Role ->
                    sessionViewModel.onAuthenticated(role)
                    when (role) {
                        Role.Student -> navController.navigateToRole(Route.Student)
                        Role.University -> navController.navigateToRole(Route.University)
                        Role.Company -> navController.navigateToRole(Route.Company)
                        Role.Admin -> navController.navigateToRole(Route.Admin)
                    }
                },
                onRegistrationClick = {
                    navController.navigate(Route.Registration)
                },
                onCareerGuidanceClick = {
                    navController.navigate(Route.CareerGuidance)
                },
            )
        }

        composable<Route.CareerGuidance> {
            CareerGuidanceNavGraph(
                isAuthenticated = false,
                onBack = navController::popBackStack,
                onRequestAuth = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Login> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onFinished = navController::popBackStack,
            )
        }

        composable<Route.Registration> {
            RegistrationScreen(
                onBack = navController::popBackStack,
                onLoginClick = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = { role ->
                    sessionViewModel.onAuthenticated(role)
                    navController.navigateToRole(role.toRoute())
                },
            )
        }

        composable<Route.Student> {
            StudentRoot(onLogout = sessionViewModel::logout)
        }

        composable<Route.University> {
            UniversityRoot(onLogout = sessionViewModel::logout)
        }

        composable<Route.Company> {
            CompanyRoot(onLogout = sessionViewModel::logout)
        }

        composable<Route.Admin> {
            AdminRoot(onLogout = sessionViewModel::logout)
        }
    }
}

private fun NavDestination.mainPageIndex(): Int? = when {
    hasRoute<Route.Login>() -> 0
    hasRoute<Route.Registration>() || hasRoute<Route.CareerGuidance>() -> 1
    hasRoute<Route.Student>() ||
        hasRoute<Route.University>() ||
        hasRoute<Route.Company>() ||
        hasRoute<Route.Admin>() -> 2
    else -> null
}

private fun Role.toRoute(): Route = when (this) {
    Role.Student -> Route.Student
    Role.University -> Route.University
    Role.Company -> Route.Company
    Role.Admin -> Route.Admin
}

private fun androidx.navigation.NavHostController.navigateToRole(
    destination: Route,
) {
    navigate(destination) {
        popUpTo<Route.Login> { inclusive = true }
    }
}

private fun androidx.navigation.NavHostController.navigateToLogin() {
    navigate(Route.Login) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
