package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.AdmissionState
import org.junit.Assert.assertEquals
import org.junit.Test

class AdmissionJourneyTest {
    @Test
    fun `unknown status keeps official application as current milestone`() {
        val milestones = AdmissionState.Unknown.toAdmissionMilestones()

        assertEquals(
            listOf(
                AdmissionMilestoneStatus.Completed,
                AdmissionMilestoneStatus.Current,
                AdmissionMilestoneStatus.Upcoming,
                AdmissionMilestoneStatus.Upcoming,
            ),
            milestones.map(AdmissionMilestone::status),
        )
    }

    @Test
    fun `recommended status advances timeline to enrollment order`() {
        val milestones = AdmissionState.Recommended.toAdmissionMilestones()

        assertEquals(
            listOf(
                AdmissionMilestoneStatus.Completed,
                AdmissionMilestoneStatus.Completed,
                AdmissionMilestoneStatus.Completed,
                AdmissionMilestoneStatus.Current,
            ),
            milestones.map(AdmissionMilestone::status),
        )
    }

    @Test
    fun `enrolled status completes every admission milestone`() {
        val statuses = AdmissionState.Enrolled.toAdmissionMilestones()
            .map(AdmissionMilestone::status)

        assertEquals(List(4) { AdmissionMilestoneStatus.Completed }, statuses)
    }
}
