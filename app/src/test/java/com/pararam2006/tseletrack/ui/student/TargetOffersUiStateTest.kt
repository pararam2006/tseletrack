package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.TargetOffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetOffersUiStateTest {
    @Test
    fun `selected offer is not duplicated in visible catalog`() {
        val selected = offer(id = "selected", directionName = "Строительство")
        val another = offer(id = "another", directionName = "Информатика")

        val state = TargetOffersUiState(
            offers = listOf(selected, another),
            selectedOffer = selected,
        )

        assertEquals(listOf(another), state.visibleOffers)
    }

    @Test
    fun `search applies after selected offer is removed`() {
        val selected = offer(id = "selected", directionName = "Информатика")
        val matching = offer(id = "matching", directionName = "Прикладная информатика")
        val other = offer(id = "other", directionName = "Строительство")

        val state = TargetOffersUiState(
            offers = listOf(selected, matching, other),
            searchQuery = "информатика",
            selectedOffer = selected,
        )

        assertEquals(listOf(matching), state.visibleOffers)
    }

    @Test
    fun `applicant code validity respects supported length and saving state`() {
        assertFalse(TargetOffersUiState(applicantCode = "12345").canSaveApplicantCode)
        assertTrue(TargetOffersUiState(applicantCode = "123456").canSaveApplicantCode)
        assertFalse(
            TargetOffersUiState(
                applicantCode = "123456",
                isSavingCode = true,
            ).canSaveApplicantCode,
        )
    }

    private fun offer(id: String, directionName: String) = TargetOffer(
        id = id,
        directionCode = null,
        directionName = directionName,
        profile = null,
        programName = null,
        programLevelName = null,
        educationFormName = null,
        universityName = "ТулГУ",
        proposerName = null,
        workplaceAddress = null,
        applicationDeadline = null,
        requestCount = null,
        requestLinkType = null,
        officialApplicationUrl = "https://trudvsem.ru/target-education/view?offeringId=$id",
        sourceUrl = "https://trudvsem.ru/target-education/view?offeringId=$id",
    )
}
