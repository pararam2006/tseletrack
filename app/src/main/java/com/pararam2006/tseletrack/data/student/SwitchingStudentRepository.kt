package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

class SwitchingStudentRepository(
    private val networkRepository: NetworkStudentRepository,
    private val demoRepository: StubStudentRepository,
    private val demoModeStore: DemoModeStore,
) : StudentRepository {
    private val activeRepository: StudentRepository
        get() = if (demoModeStore.role == com.pararam2006.tseletrack.core.Role.Student) {
            demoRepository
        } else {
            networkRepository
        }

    override suspend fun loadDashboard(): StudentDashboard = activeRepository.loadDashboard()

    override suspend fun loadDocuments(): List<StudentDocument> = activeRepository.loadDocuments()

    override suspend fun uploadContract(document: StudentDocumentUpload) =
        activeRepository.uploadContract(document)

    override suspend fun downloadDocument(document: StudentDocument): DownloadedDocument =
        activeRepository.downloadDocument(document)

    override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog =
        activeRepository.loadTargetOffers(forceRefresh)

    override suspend fun loadTargetOfferSelection(): TargetOfferSelection? =
        activeRepository.loadTargetOfferSelection()

    override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection =
        activeRepository.chooseTargetOffer(offerId)

    override suspend fun loadAdmissionStatus(): AdmissionStatus? =
        activeRepository.loadAdmissionStatus()

    override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus =
        activeRepository.saveApplicantCode(applicantCode)

    override suspend fun refreshAdmissionStatus(): AdmissionStatus =
        activeRepository.refreshAdmissionStatus()

    override suspend fun completeCareerGuidance() = activeRepository.completeCareerGuidance()

    override suspend fun logout() {
        val repository = activeRepository
        demoModeStore.stopDemo()
        repository.logout()
    }
}
