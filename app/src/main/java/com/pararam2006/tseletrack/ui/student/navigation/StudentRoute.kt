package com.pararam2006.tseletrack.ui.student.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

sealed interface StudentRoute {
    @Serializable
    data object Roadmap : StudentRoute

    @Serializable
    data object TargetOffers : StudentRoute

    @Serializable
    data object CareerGuidance : StudentRoute

    @Serializable
    data object Documents : StudentRoute

    @Serializable
    data object AcademicPerformance : StudentRoute

    @Serializable
    data class EmployerEvents(val organizationName: String) : StudentRoute

    @Serializable
    data object Profile : StudentRoute
}

internal fun NavDestination.toStudentRoute(): StudentRoute? = when {
    hasRoute<StudentRoute.Roadmap>() -> StudentRoute.Roadmap
    hasRoute<StudentRoute.TargetOffers>() -> StudentRoute.TargetOffers
    hasRoute<StudentRoute.CareerGuidance>() -> StudentRoute.CareerGuidance
    hasRoute<StudentRoute.Documents>() -> StudentRoute.Documents
    hasRoute<StudentRoute.AcademicPerformance>() -> StudentRoute.AcademicPerformance
    hasRoute<StudentRoute.EmployerEvents>() -> null
    hasRoute<StudentRoute.Profile>() -> StudentRoute.Profile
    else -> null
}

internal fun NavDestination.studentPageIndex(): Int? = when {
    hasRoute<StudentRoute.Roadmap>() -> 0
    hasRoute<StudentRoute.TargetOffers>() -> 1
    hasRoute<StudentRoute.Documents>() -> 2
    hasRoute<StudentRoute.Profile>() -> 3
    else -> null
}

internal fun NavHostController.navigateToStudentRoute(route: StudentRoute) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
