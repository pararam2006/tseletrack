package com.pararam2006.tseletrack.ui.university

import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import org.junit.Assert.assertEquals
import org.junit.Test

class UniversityUiStateTest {
    @Test
    fun `underfilled filter keeps only offers below quota`() {
        val underfilled = metric("under", 5, 2)
        val over = metric("over", 5, 8)
        val state = UniversityUiState(
            dashboard = dashboard(underfilled, over),
            selectedOfferDemandFilter = OfferDemandFilter.Underfilled,
        )

        assertEquals(listOf(underfilled), state.visibleTargetOfferMetrics)
    }

    @Test
    fun `all offers are ordered by operational priority`() {
        val filled = metric("filled", 5, 5)
        val over = metric("over", 5, 8)
        val underfilled = metric("under", 5, 2)
        val state = UniversityUiState(dashboard = dashboard(filled, over, underfilled))

        assertEquals(
            listOf(underfilled, over, filled),
            state.visibleTargetOfferMetrics,
        )
    }

    private fun metric(id: String, places: Int, applications: Int) = TargetOfferMetric(
        offerId = id,
        directionCode = "09.03.01",
        directionName = id,
        companyName = "Компания",
        quotaPlaces = places,
        applicationCount = applications,
        updatedAt = "Сейчас",
    )

    private fun dashboard(vararg metrics: TargetOfferMetric) = UniversityDashboard(
        studentCount = 0,
        queueCount = 0,
        availablePlaces = 0,
        queue = emptyList(),
        students = emptyList(),
        analytics = AnalyticsDashboard(0, 0, 0, 0, emptyList()),
        targetOfferMetrics = metrics.toList(),
        profile = RoleProfile("ТулГУ", "Т", "Вуз"),
    )
}
