package com.pararam2006.tseletrack

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.core.Route
import com.pararam2006.tseletrack.ui.company.CompanyScreen
import com.pararam2006.tseletrack.ui.login.LoginScreen
import com.pararam2006.tseletrack.ui.region.RegionScreen
import com.pararam2006.tseletrack.ui.student.StudentScreen
import com.pararam2006.tseletrack.ui.university.UniversityScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Route.Login) {
        composable<Route.Login> {
            LoginScreen(onLoginSuccess = { role: Role ->
                when (role) {
                    Role.Student -> navController.navigate(Route.Student)
                    Role.University -> navController.navigate(Route.University)
                    Role.Company -> navController.navigate(Route.Company)
                    Role.Admin -> navController.navigate(Route.Region)
                }
            })
        }

        composable<Route.Student> {
            StudentScreen()
        }

        composable<Route.University> {
            UniversityScreen()
        }

        composable<Route.Company> {
            CompanyScreen()
        }

        composable<Route.Region> {
            RegionScreen()
        }
    }
}

