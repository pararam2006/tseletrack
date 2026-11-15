package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus

internal enum class RoutePrimaryAction {
    CareerGuidance,
    ChooseTargetOffer,
    SubmitDocuments,
    CheckApplication,
    TrackApplication,
    CheckEnrollmentOrder,
    EnrollmentConfirmed,
    UploadContract,
    FixContract,
    ContractLocked,
    ContractPending,
    ContractAccepted,
    AcademicPerformance,
    EmploymentDocuments,
    CurrentStageDocuments,
    Waiting,
    Completed,
    ;

    val isEnabled: Boolean
        get() = when (this) {
            CareerGuidance,
            ChooseTargetOffer,
            SubmitDocuments,
            CheckApplication,
            TrackApplication,
            CheckEnrollmentOrder,
            EnrollmentConfirmed,
            UploadContract,
            FixContract,
            AcademicPerformance,
            EmploymentDocuments,
            CurrentStageDocuments,
            -> true

            ContractLocked,
            ContractPending,
            ContractAccepted,
            Waiting,
            Completed,
            -> false
        }
}

internal fun List<RouteStage>.primaryAction(
    contractStatus: StudentDocumentStatus,
    hasSelectedOffer: Boolean,
    admissionState: AdmissionState?,
): RoutePrimaryAction {
    val currentStage = firstOrNull { it.status == RouteStageStatus.Current }
        ?: return if (isNotEmpty() && all { it.status == RouteStageStatus.Completed }) {
            RoutePrimaryAction.Completed
        } else {
            RoutePrimaryAction.Waiting
        }

    return when (currentStage.type) {
        RouteStageType.CareerGuidance -> RoutePrimaryAction.CareerGuidance
        RouteStageType.TargetOfferChoice -> when {
            !hasSelectedOffer -> RoutePrimaryAction.ChooseTargetOffer
            admissionState == null -> RoutePrimaryAction.SubmitDocuments
            admissionState == AdmissionState.Unknown -> RoutePrimaryAction.CheckApplication
            admissionState == AdmissionState.ApplicationFound -> RoutePrimaryAction.TrackApplication
            admissionState == AdmissionState.Recommended -> RoutePrimaryAction.CheckEnrollmentOrder
            else -> RoutePrimaryAction.EnrollmentConfirmed
        }

        RouteStageType.Contract -> when {
            admissionState != AdmissionState.Enrolled -> RoutePrimaryAction.ContractLocked
            contractStatus == StudentDocumentStatus.UnderReview -> RoutePrimaryAction.ContractPending
            contractStatus == StudentDocumentStatus.ChangesRequested -> RoutePrimaryAction.FixContract
            contractStatus == StudentDocumentStatus.Accepted -> RoutePrimaryAction.ContractAccepted
            else -> RoutePrimaryAction.UploadContract
        }

        RouteStageType.Education -> RoutePrimaryAction.AcademicPerformance
        RouteStageType.Employment -> RoutePrimaryAction.EmploymentDocuments
        RouteStageType.Other -> RoutePrimaryAction.CurrentStageDocuments
    }
}
