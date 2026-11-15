package com.pararam2006.tseletrack.data.demo

import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.events.EventParticipant

class DemoPlatformStore {
    var selectedOfferId: String? = null
    var applicantCode: String? = null
    var admissionState: AdmissionState? = null
    var contractStatus: StudentDocumentStatus = StudentDocumentStatus.AwaitingUpload
    var contractFileName: String? = null
    private val academicPerformanceByStudentId: MutableMap<String, AcademicPerformance> =
        mutableMapOf()
    val resolvedContractIds: MutableSet<String> = mutableSetOf()
    val eventRegistrations: MutableMap<String, MutableMap<String, EventParticipant>> =
        mutableMapOf()

    fun reset() {
        selectedOfferId = null
        applicantCode = null
        admissionState = null
        contractStatus = StudentDocumentStatus.AwaitingUpload
        contractFileName = null
        academicPerformanceByStudentId.clear()
        resolvedContractIds.clear()
        eventRegistrations.clear()
    }

    fun selectOffer(offerId: String) {
        selectedOfferId = offerId
        applicantCode = null
        admissionState = null
        contractStatus = StudentDocumentStatus.AwaitingUpload
        contractFileName = null
    }

    fun academicPerformance(studentId: String): AcademicPerformance? =
        academicPerformanceByStudentId[studentId]

    fun saveAcademicPerformance(
        studentId: String,
        performance: AcademicPerformance,
    ) {
        academicPerformanceByStudentId[studentId] = performance
    }

    fun isApplicationCountedFor(offerId: String): Boolean =
        selectedOfferId == offerId && when (admissionState) {
            AdmissionState.ApplicationFound,
            AdmissionState.Recommended,
            AdmissionState.Enrolled,
            -> true

            null,
            AdmissionState.Unknown,
            -> false
        }
}
