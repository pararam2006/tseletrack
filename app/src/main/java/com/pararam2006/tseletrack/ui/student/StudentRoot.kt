package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.common.AppBottomBar
import com.pararam2006.tseletrack.ui.common.AppBottomTab
import com.pararam2006.tseletrack.ui.student.navigation.StudentNavGraph
import com.pararam2006.tseletrack.ui.student.navigation.StudentRoute
import com.pararam2006.tseletrack.ui.student.navigation.navigateToStudentRoute
import com.pararam2006.tseletrack.ui.student.navigation.toStudentRoute
import com.pararam2006.tseletrack.ui.theme.tseletrackBg

private val studentTabs: List<AppBottomTab<StudentRoute>> = listOf(
    AppBottomTab(StudentRoute.Roadmap, R.string.tab_route, R.drawable.ic_nav_route, "student_tab_route"),
    AppBottomTab(StudentRoute.TargetOffers, R.string.tab_target_offers, R.drawable.ic_nav_university, "student_tab_target_offers"),
    AppBottomTab(StudentRoute.Documents, R.string.tab_documents, R.drawable.ic_nav_queue, "student_tab_documents"),
    AppBottomTab(StudentRoute.Profile, R.string.tab_profile, R.drawable.ic_nav_student_profile, "student_tab_profile"),
)

@Composable
fun StudentRoot(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedRoute = backStackEntry?.destination?.toStudentRoute()
    val showBottomBar = studentTabs.any { it.value == selectedRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = tseletrackBg,
        bottomBar = {
            if (showBottomBar && selectedRoute != null) {
                AppBottomBar(
                    tabs = studentTabs,
                    selectedTab = selectedRoute,
                    onTabSelected = navController::navigateToStudentRoute,
                )
            }
        },
    ) { innerPadding ->
        StudentNavGraph(
            navController = navController,
            onLogout = onLogout,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}
