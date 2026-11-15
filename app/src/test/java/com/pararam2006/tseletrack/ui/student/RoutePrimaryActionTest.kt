package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutePrimaryActionTest {
    @Test
    fun `current stage maps to its primary action`() {
        val expectedActions = mapOf(
            RouteStageType.CareerGuidance to RoutePrimaryAction.CareerGuidance,
            RouteStageType.Education to RoutePrimaryAction.AcademicPerformance,
            RouteStageType.Employment to RoutePrimaryAction.EmploymentDocuments,
            RouteStageType.Other to RoutePrimaryAction.CurrentStageDocuments,
        )

        expectedActions.forEach { (stageType, expectedAction) ->
            assertEquals(
                expectedAction,
                listOf(stage(stageType, RouteStageStatus.Current)).primaryAction(
                    contractStatus = StudentDocumentStatus.AwaitingUpload,
                    hasSelectedOffer = true,
                    admissionState = AdmissionState.Enrolled,
                ),
            )
        }
    }

    @Test
    fun `contract status controls route action`() {
        val contractStage = listOf(stage(RouteStageType.Contract, RouteStageStatus.Current))

        assertEquals(
            RoutePrimaryAction.UploadContract,
            contractStage.primaryAction(StudentDocumentStatus.AwaitingUpload, true, AdmissionState.Enrolled),
        )
        assertEquals(
            RoutePrimaryAction.ContractPending,
            contractStage.primaryAction(StudentDocumentStatus.UnderReview, true, AdmissionState.Enrolled),
        )
        assertEquals(
            RoutePrimaryAction.FixContract,
            contractStage.primaryAction(StudentDocumentStatus.ChangesRequested, true, AdmissionState.Enrolled),
        )
        assertEquals(
            RoutePrimaryAction.ContractAccepted,
            contractStage.primaryAction(StudentDocumentStatus.Accepted, true, AdmissionState.Enrolled),
        )
    }

    @Test
    fun `target offer stage follows admission progress`() {
        val targetStage = listOf(stage(RouteStageType.TargetOfferChoice, RouteStageStatus.Current))
        val status = StudentDocumentStatus.Unavailable

        assertEquals(RoutePrimaryAction.ChooseTargetOffer, targetStage.primaryAction(status, false, null))
        assertEquals(RoutePrimaryAction.SubmitDocuments, targetStage.primaryAction(status, true, null))
        assertEquals(
            RoutePrimaryAction.CheckApplication,
            targetStage.primaryAction(status, true, AdmissionState.Unknown),
        )
        assertEquals(
            RoutePrimaryAction.TrackApplication,
            targetStage.primaryAction(status, true, AdmissionState.ApplicationFound),
        )
        assertEquals(
            RoutePrimaryAction.CheckEnrollmentOrder,
            targetStage.primaryAction(status, true, AdmissionState.Recommended),
        )
        assertEquals(
            RoutePrimaryAction.EnrollmentConfirmed,
            targetStage.primaryAction(status, true, AdmissionState.Enrolled),
        )
    }

    @Test
    fun `contract remains locked until official enrollment`() {
        val contractStage = listOf(stage(RouteStageType.Contract, RouteStageStatus.Current))

        assertEquals(
            RoutePrimaryAction.ContractLocked,
            contractStage.primaryAction(
                StudentDocumentStatus.AwaitingUpload,
                true,
                AdmissionState.Recommended,
            ),
        )
    }

    @Test
    fun `route without current stage distinguishes completed and waiting`() {
        val completed = listOf(
            stage(RouteStageType.CareerGuidance, RouteStageStatus.Completed),
            stage(RouteStageType.Employment, RouteStageStatus.Completed),
        )
        val waiting = listOf(
            stage(RouteStageType.CareerGuidance, RouteStageStatus.Completed),
            stage(RouteStageType.Contract, RouteStageStatus.Waiting),
        )
        val status = StudentDocumentStatus.Unavailable

        assertEquals(RoutePrimaryAction.Completed, completed.primaryAction(status, true, AdmissionState.Enrolled))
        assertEquals(RoutePrimaryAction.Waiting, waiting.primaryAction(status, true, AdmissionState.Enrolled))
        assertEquals(RoutePrimaryAction.Waiting, emptyList<RouteStage>().primaryAction(status, false, null))
    }

    private fun stage(type: RouteStageType, status: RouteStageStatus) = RouteStage(
        id = type.name,
        type = type,
        title = type.name,
        status = status,
    )
}
