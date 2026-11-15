package com.pararam2006.tseletrack.domain.role

import org.junit.Assert.assertEquals
import org.junit.Test

class TargetOfferMetricTest {
    @Test
    fun `demand status and delta distinguish underfill exact plan and oversubscription`() {
        val underfilled = metric(places = 5, applications = 3)
        val filled = metric(places = 5, applications = 5)
        val oversubscribed = metric(places = 5, applications = 10)

        assertEquals(OfferDemandStatus.Underfilled, underfilled.demandStatus)
        assertEquals(-2, underfilled.delta)
        assertEquals(60, underfilled.fillPercent)
        assertEquals(OfferDemandStatus.Filled, filled.demandStatus)
        assertEquals(OfferDemandStatus.Oversubscribed, oversubscribed.demandStatus)
        assertEquals(5, oversubscribed.delta)
        assertEquals(200, oversubscribed.fillPercent)
    }

    private fun metric(places: Int, applications: Int) = TargetOfferMetric(
        offerId = "offer",
        directionCode = "09.03.01",
        directionName = "Информатика",
        companyName = "АО «КБП»",
        quotaPlaces = places,
        applicationCount = applications,
        updatedAt = "Сейчас",
    )
}
