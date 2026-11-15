package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

interface StudentRepository {
    suspend fun loadDashboard(): StudentDashboard
    suspend fun loadDocuments(): List<StudentDocument>
    suspend fun uploadContract(document: StudentDocumentUpload)
    suspend fun downloadDocument(document: StudentDocument): DownloadedDocument
    suspend fun loadTargetOffers(forceRefresh: Boolean = false): TargetOfferCatalog
    suspend fun loadTargetOfferSelection(): TargetOfferSelection?
    suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection
    suspend fun loadAdmissionStatus(): AdmissionStatus?
    suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus
    suspend fun refreshAdmissionStatus(): AdmissionStatus
    suspend fun completeCareerGuidance()
    suspend fun logout()
}
