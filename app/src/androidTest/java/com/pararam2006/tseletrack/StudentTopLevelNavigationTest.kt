package com.pararam2006.tseletrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pararam2006.tseletrack.ui.student.navigation.StudentRoute
import com.pararam2006.tseletrack.ui.student.navigation.navigateToStudentRoute
import org.junit.Rule
import org.junit.Test

class StudentTopLevelNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun roadmapRemainsReachableAfterOpeningOffersFromRoadmap() {
        composeRule.setContent { NavigationScenario() }

        composeRule.onNodeWithText(OFFERS_TAB).performClick()
        composeRule.onNodeWithText(OFFERS_SCREEN).assertIsDisplayed()

        composeRule.onNodeWithText(ROADMAP_TAB).performClick()
        composeRule.onNodeWithText(ROADMAP_SCREEN).assertIsDisplayed()

        composeRule.onNodeWithText(OPEN_OFFERS_FROM_ROADMAP).performClick()
        composeRule.onNodeWithText(OFFERS_SCREEN).assertIsDisplayed()

        composeRule.onNodeWithText(ROADMAP_TAB).performClick()
        composeRule.onNodeWithText(ROADMAP_SCREEN).assertIsDisplayed()
    }

    @Composable
    private fun NavigationScenario() {
        val navController = rememberNavController()

        Column {
            Text(
                text = ROADMAP_TAB,
                modifier = Modifier.clickable {
                    navController.navigateToStudentRoute(StudentRoute.Roadmap)
                },
            )
            Text(
                text = OFFERS_TAB,
                modifier = Modifier.clickable {
                    navController.navigateToStudentRoute(StudentRoute.TargetOffers)
                },
            )
            NavHost(
                navController = navController,
                startDestination = StudentRoute.Roadmap,
            ) {
                composable<StudentRoute.Roadmap> {
                    Column {
                        Text(ROADMAP_SCREEN)
                        Text(
                            text = OPEN_OFFERS_FROM_ROADMAP,
                            modifier = Modifier.clickable {
                                navController.navigateToStudentRoute(StudentRoute.TargetOffers)
                            },
                        )
                    }
                }
                composable<StudentRoute.TargetOffers> {
                    Text(OFFERS_SCREEN)
                }
            }
        }
    }

    private companion object {
        const val ROADMAP_TAB = "Вкладка Маршрут"
        const val OFFERS_TAB = "Вкладка Предложения"
        const val ROADMAP_SCREEN = "Экран маршрута"
        const val OFFERS_SCREEN = "Экран предложений"
        const val OPEN_OFFERS_FROM_ROADMAP = "Открыть предложения из маршрута"
    }
}
