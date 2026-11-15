package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.student.StubStudentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DemoUniversityAnalyticsTest {
    @Test
    fun `officially found student application increments the same offer analytics`() = runTest {
        val store = DemoPlatformStore()
        val studentRepository = StubStudentRepository(store)
        val universityRepository = StubUniversityRepository(store)
        val offer = studentRepository.loadTargetOffers().items.first()
        val before = universityRepository.loadDashboard()
            .targetOfferMetrics.single { it.offerId == offer.id }

        studentRepository.chooseTargetOffer(offer.id)
        studentRepository.saveApplicantCode("12345678")
        studentRepository.refreshAdmissionStatus()

        val after = universityRepository.loadDashboard()
            .targetOfferMetrics.single { it.offerId == offer.id }
        assertEquals(before.applicationCount + 1, after.applicationCount)
    }

    @Test
    fun `locally saved offer alone does not count as submitted application`() = runTest {
        val store = DemoPlatformStore()
        val studentRepository = StubStudentRepository(store)
        val universityRepository = StubUniversityRepository(store)
        val offer = studentRepository.loadTargetOffers().items.first()
        val before = universityRepository.loadDashboard()
            .targetOfferMetrics.single { it.offerId == offer.id }

        studentRepository.chooseTargetOffer(offer.id)

        val after = universityRepository.loadDashboard()
            .targetOfferMetrics.single { it.offerId == offer.id }
        assertEquals(before.applicationCount, after.applicationCount)
    }
}
