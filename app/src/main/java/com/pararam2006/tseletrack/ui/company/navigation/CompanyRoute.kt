package com.pararam2006.tseletrack.ui.company.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

sealed interface CompanyRoute {
    @Serializable data object Candidates : CompanyRoute
    @Serializable data object Events : CompanyRoute
    @Serializable data object Analytics : CompanyRoute
    @Serializable data object Profile : CompanyRoute
}

internal fun NavDestination.toCompanyRoute(): CompanyRoute? = when {
    hasRoute<CompanyRoute.Candidates>() -> CompanyRoute.Candidates
    hasRoute<CompanyRoute.Events>() -> CompanyRoute.Events
    hasRoute<CompanyRoute.Analytics>() -> CompanyRoute.Analytics
    hasRoute<CompanyRoute.Profile>() -> CompanyRoute.Profile
    else -> null
}
