package com.pararam2006.tseletrack.ui.student.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

sealed interface StudentRoute {
    @Serializable
    data object Roadmap : StudentRoute

    @Serializable
    data object Universities : StudentRoute

    @Serializable
    data object Tasks : StudentRoute

    @Serializable
    data object Profile : StudentRoute
}

internal fun NavDestination.toStudentRoute(): StudentRoute? = when {
    hasRoute<StudentRoute.Roadmap>() -> StudentRoute.Roadmap
    hasRoute<StudentRoute.Universities>() -> StudentRoute.Universities
    hasRoute<StudentRoute.Tasks>() -> StudentRoute.Tasks
    hasRoute<StudentRoute.Profile>() -> StudentRoute.Profile
    else -> null
}
