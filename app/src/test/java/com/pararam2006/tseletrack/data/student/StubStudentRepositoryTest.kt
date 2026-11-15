package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.university.StubUniversityRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class StubStudentRepositoryTest {
    @Test
    fun `demo journey advances from offer selection to education without network`() = runTest {
        val store = DemoPlatformStore()
        val repository = StubStudentRepository(store)
        val universityRepository = StubUniversityRepository(store)

        assertNull(repository.loadTargetOfferSelection())
        val offer = repository.loadTargetOffers().items.first()
        repository.chooseTargetOffer(offer.id)
        assertNotNull(repository.loadTargetOfferSelection())

        assertEquals(
            AdmissionState.Unknown,
            repository.saveApplicantCode("12345678").state,
        )
        assertEquals(AdmissionState.ApplicationFound, repository.refreshAdmissionStatus().state)
        assertEquals(AdmissionState.Recommended, repository.refreshAdmissionStatus().state)
        assertEquals(AdmissionState.Enrolled, repository.refreshAdmissionStatus().state)

        val enrolledDashboard = repository.loadDashboard()
        assertEquals(
            RouteStageStatus.Current,
            enrolledDashboard.routeStages.single { it.type == RouteStageType.Contract }.status,
        )

        repository.uploadContract(StudentDocumentUpload("contract.pdf", "application/pdf", 1024))

        val uploadedContract = repository.loadDocuments()
            .single { it.type == StudentDocumentType.TargetContract }
        assertEquals(StudentDocumentStatus.UnderReview, uploadedContract.status)
        val queueItem = universityRepository.loadDashboard().queue
            .single { it.studentName == "Демо-абитуриент" }
        assertEquals("contract.pdf", queueItem.fileName)

        universityRepository.confirmContract(queueItem.id)

        val confirmedContract = repository.loadDocuments()
            .single { it.type == StudentDocumentType.TargetContract }
        assertEquals(StudentDocumentStatus.Accepted, confirmedContract.status)
        assertNotNull(confirmedContract.downloadUrl)
        assertEquals(
            "contract.pdf",
            repository.downloadDocument(confirmedContract).fileName,
        )

        val signedDashboard = repository.loadDashboard()
        assertEquals(
            RouteStageStatus.Current,
            signedDashboard.routeStages.single { it.type == RouteStageType.Education }.status,
        )
    }

    @Test
    fun `demo contract cannot be uploaded before enrollment`() {
        val repository = StubStudentRepository()

        assertThrows(IllegalStateException::class.java) {
            kotlinx.coroutines.runBlocking {
                repository.uploadContract(StudentDocumentUpload("contract.pdf", "application/pdf", 1024))
            }
        }
    }

    @Test
    fun `university can request contract changes`() = runTest {
        val store = DemoPlatformStore().apply {
            admissionState = AdmissionState.Enrolled
        }
        val studentRepository = StubStudentRepository(store)
        val universityRepository = StubUniversityRepository(store)

        studentRepository.uploadContract(StudentDocumentUpload("contract.pdf", "application/pdf", 1024))
        val queueItem = universityRepository.loadDashboard().queue
            .single { it.studentName == "Демо-абитуриент" }
        universityRepository.declineContract(queueItem.id)

        assertEquals(
            StudentDocumentStatus.ChangesRequested,
            studentRepository.loadDocuments()
                .single { it.type == StudentDocumentType.TargetContract }
                .status,
        )
    }

    @Test
    fun `changing demo offer clears previous admission tracking`() = runTest {
        val repository = StubStudentRepository()
        val offers = repository.loadTargetOffers().items
        repository.chooseTargetOffer(offers.first().id)
        repository.saveApplicantCode("12345678")
        repository.refreshAdmissionStatus()

        repository.chooseTargetOffer(offers.last().id)

        assertNull(repository.loadAdmissionStatus())
        assertEquals(offers.last().id, repository.loadTargetOfferSelection()?.offer?.id)
    }

    @Test
    fun `reset starts a new demo student without previous academic data or files`() = runTest {
        val store = DemoPlatformStore().apply {
            admissionState = AdmissionState.Enrolled
        }
        val studentRepository = StubStudentRepository(store)
        val academicRepository = StubAcademicPerformanceRepository(store)
        studentRepository.uploadContract(
            StudentDocumentUpload("first-student.pdf", "application/pdf", 1024),
        )
        academicRepository.connect("12345678", "ИВТ-221")

        store.reset()

        assertNull(academicRepository.load())
        assertNull(store.contractFileName)
        assertEquals(
            StudentDocumentStatus.Unavailable,
            studentRepository.loadDocuments()
                .single { it.type == StudentDocumentType.TargetContract }
                .status,
        )
    }
}
