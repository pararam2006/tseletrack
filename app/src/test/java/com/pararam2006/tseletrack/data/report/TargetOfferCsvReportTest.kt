package com.pararam2006.tseletrack.data.report

import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetOfferCsvReportTest {
    @Test
    fun `report is excel friendly and escapes organization names`() {
        val report = TargetOfferCsvReport.create(
            listOf(
                TargetOfferMetric(
                    offerId = "offer-1",
                    directionCode = "09.03.01",
                    directionName = "Информатика",
                    companyName = "АО \"КБП; Тула\"",
                    quotaPlaces = 5,
                    applicationCount = 10,
                    updatedAt = "Только что",
                ),
            ),
        )

        assertTrue(report.startsWith("\uFEFF"))
        assertTrue(report.contains("\"АО \"\"КБП; Тула\"\"\""))
        assertTrue(report.contains("\"5\";\"10\";\"5\";\"Переподписка\""))
    }
}
