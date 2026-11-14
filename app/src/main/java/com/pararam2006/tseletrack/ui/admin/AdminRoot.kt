package com.pararam2006.tseletrack.ui.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.admin.navigation.AdminNavGraph
import com.pararam2006.tseletrack.ui.admin.navigation.AdminRoute
import com.pararam2006.tseletrack.ui.admin.navigation.toAdminRoute
import com.pararam2006.tseletrack.ui.common.AppBottomBar
import com.pararam2006.tseletrack.ui.common.AppBottomTab
import com.pararam2006.tseletrack.ui.theme.tseletrackBg

private val adminTabs: List<AppBottomTab<AdminRoute>> = listOf(
    AppBottomTab(AdminRoute.Overview, R.string.tab_overview, R.drawable.ic_nav_overview, "admin_tab_overview"),
    AppBottomTab(AdminRoute.Users, R.string.tab_users, R.drawable.ic_nav_users, "admin_tab_users"),
    AppBottomTab(AdminRoute.Data, R.string.tab_data, R.drawable.ic_nav_data, "admin_tab_data"),
    AppBottomTab(AdminRoute.Profile, R.string.tab_profile, R.drawable.ic_nav_profile, "admin_tab_profile"),
)

@Composable
fun AdminRoot(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedRoute = backStackEntry?.destination?.toAdminRoute() ?: AdminRoute.Overview

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = tseletrackBg,
        bottomBar = {
            AppBottomBar(
                tabs = adminTabs,
                selectedTab = selectedRoute,
                onTabSelected = navController::navigateToAdminRoute,
            )
        },
    ) { innerPadding ->
        AdminNavGraph(
            navController = navController,
            onUsersClick = { navController.navigateToAdminRoute(AdminRoute.Users) },
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}

private fun NavHostController.navigateToAdminRoute(route: AdminRoute) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
