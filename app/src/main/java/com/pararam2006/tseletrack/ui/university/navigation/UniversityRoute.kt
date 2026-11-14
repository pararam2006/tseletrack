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
