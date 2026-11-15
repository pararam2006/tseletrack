package com.pararam2006.tseletrack.ui.university.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

sealed interface UniversityRoute {
    @Serializable data object Queue : UniversityRoute
    @Serializable data object Students : UniversityRoute
    @Serializable data object Analytics : UniversityRoute
    @Serializable data object Profile : UniversityRoute
}

internal fun NavDestination.toUniversityRoute(): UniversityRoute? = when {
    hasRoute<UniversityRoute.Queue>() -> UniversityRoute.Queue
    hasRoute<UniversityRoute.Students>() -> UniversityRoute.Students
    hasRoute<UniversityRoute.Analytics>() -> UniversityRoute.Analytics
    hasRoute<UniversityRoute.Profile>() -> UniversityRoute.Profile
    else -> null
}

internal fun NavDestination.universityPageIndex(): Int? = when {
    hasRoute<UniversityRoute.Analytics>() -> 0
    hasRoute<UniversityRoute.Queue>() -> 1
    hasRoute<UniversityRoute.Students>() -> 2
    hasRoute<UniversityRoute.Profile>() -> 3
    else -> null
}
