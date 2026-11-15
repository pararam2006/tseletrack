package com.pararam2006.tseletrack.ui.admin.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable

sealed interface AdminRoute {
    @Serializable data object Overview : AdminRoute
    @Serializable data object Users : AdminRoute
    @Serializable data object Data : AdminRoute
    @Serializable data object Profile : AdminRoute
}

internal fun NavDestination.toAdminRoute(): AdminRoute? = when {
    hasRoute<AdminRoute.Overview>() -> AdminRoute.Overview
    hasRoute<AdminRoute.Users>() -> AdminRoute.Users
    hasRoute<AdminRoute.Data>() -> AdminRoute.Data
    hasRoute<AdminRoute.Profile>() -> AdminRoute.Profile
    else -> null
}

internal fun NavDestination.adminPageIndex(): Int? = when {
    hasRoute<AdminRoute.Overview>() -> 0
    hasRoute<AdminRoute.Users>() -> 1
    hasRoute<AdminRoute.Data>() -> 2
    hasRoute<AdminRoute.Profile>() -> 3
    else -> null
}
