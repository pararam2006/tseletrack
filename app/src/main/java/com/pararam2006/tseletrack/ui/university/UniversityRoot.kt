package com.pararam2006.tseletrack.ui.university

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
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.university.navigation.UniversityNavGraph
import com.pararam2006.tseletrack.ui.university.navigation.UniversityRoute
import com.pararam2006.tseletrack.ui.university.navigation.toUniversityRoute

private val universityTabs: List<AppBottomTab<UniversityRoute>> = listOf(
    AppBottomTab(UniversityRoute.Queue, R.string.tab_queue, R.drawable.ic_nav_queue, "university_tab_queue"),
    AppBottomTab(UniversityRoute.Students, R.string.tab_students, R.drawable.ic_nav_students, "university_tab_students"),
    AppBottomTab(UniversityRoute.Analytics, R.string.tab_analytics, R.drawable.ic_nav_analytics, "university_tab_analytics"),
    AppBottomTab(UniversityRoute.Profile, R.string.tab_profile, R.drawable.ic_nav_profile, "university_tab_profile"),
)

@Composable
fun UniversityRoot(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedRoute = backStackEntry?.destination?.toUniversityRoute() ?: UniversityRoute.Queue

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = tseletrackBg,
        bottomBar = {
            AppBottomBar(
                tabs = universityTabs,
                selectedTab = selectedRoute,
                onTabSelected = navController::navigateToUniversityRoute,
            )
        },
    ) { innerPadding ->
        UniversityNavGraph(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}

private fun NavHostController.navigateToUniversityRoute(route: UniversityRoute) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
