package com.pararam2006.tseletrack.ui.company

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
import com.pararam2006.tseletrack.ui.common.AppBottomBar
import com.pararam2006.tseletrack.ui.common.AppBottomTab
import com.pararam2006.tseletrack.ui.company.navigation.CompanyNavGraph
import com.pararam2006.tseletrack.ui.company.navigation.CompanyRoute
import com.pararam2006.tseletrack.ui.company.navigation.toCompanyRoute
import com.pararam2006.tseletrack.ui.theme.tseletrackBg

private val companyTabs: List<AppBottomTab<CompanyRoute>> = listOf(
    AppBottomTab(CompanyRoute.Candidates, R.string.tab_candidates, R.drawable.ic_nav_students, "company_tab_candidates"),
    AppBottomTab(CompanyRoute.Events, R.string.tab_events, R.drawable.ic_nav_queue, "company_tab_events"),
    AppBottomTab(CompanyRoute.Analytics, R.string.tab_analytics, R.drawable.ic_nav_analytics, "company_tab_analytics"),
    AppBottomTab(CompanyRoute.Profile, R.string.tab_profile, R.drawable.ic_nav_profile, "company_tab_profile"),
)

@Composable
fun CompanyRoot(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedRoute = backStackEntry?.destination?.toCompanyRoute() ?: CompanyRoute.Candidates

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = tseletrackBg,
        bottomBar = {
            AppBottomBar(
                tabs = companyTabs,
                selectedTab = selectedRoute,
                onTabSelected = navController::navigateToCompanyRoute,
            )
        },
    ) { innerPadding ->
        CompanyNavGraph(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}

private fun NavHostController.navigateToCompanyRoute(route: CompanyRoute) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
