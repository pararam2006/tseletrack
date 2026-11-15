package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileAccessTest {
    @Test
    fun `current education stage exposes academic performance`() {
        assertTrue(listOf(stage(RouteStageStatus.Current)).hasAcademicPerformanceAccess())
    }

    @Test
    fun `completed education stage keeps academic performance available`() {
        assertTrue(listOf(stage(RouteStageStatus.Completed)).hasAcademicPerformanceAccess())
    }

    @Test
    fun `future education stage hides academic performance`() {
        assertFalse(listOf(stage(RouteStageStatus.Future)).hasAcademicPerformanceAccess())
    }

    private fun stage(status: RouteStageStatus) = RouteStage(
        id = "education",
        type = RouteStageType.Education,
        title = "Обучение",
        status = status,
    )
}
