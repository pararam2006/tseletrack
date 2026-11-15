package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import com.pararam2006.tseletrack.ui.university.OfferDemandFilter
import com.pararam2006.tseletrack.ui.university.UniversityAdmissionAnalyticsScreen
import org.junit.Rule
import org.junit.Test

class UniversityAdmissionAnalyticsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun analyticsShowsUnderfillAndOversubscription() {
        val metrics = listOf(
            metric("АО «КБП»", places = 20, applications = 11),
            metric("АО «Сплав»", places = 5, applications = 10),
        )
        composeRule.setContent {
            TseletrackTheme {
                UniversityAdmissionAnalyticsScreen(
                    metrics = metrics,
                    visibleMetrics = metrics,
                    selectedFilter = OfferDemandFilter.All,
                    onFilterSelected = {},
                )
            }
        }

        composeRule.onAllNodesWithText("Недобор").assertCountEquals(2)
        composeRule.onAllNodesWithText("Переподписка").assertCountEquals(2)
        composeRule.onNodeWithText("Не хватает 9").assertIsDisplayed()
        composeRule.onNodeWithText("Сверх плана: 5").assertIsDisplayed()
    }

    private fun metric(company: String, places: Int, applications: Int) = TargetOfferMetric(
        offerId = company,
        directionCode = "09.03.01",
        directionName = "Информатика",
        companyName = company,
        quotaPlaces = places,
        applicationCount = applications,
        updatedAt = "Только что",
    )
}
