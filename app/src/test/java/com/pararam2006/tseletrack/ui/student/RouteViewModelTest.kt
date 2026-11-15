package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RouteViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `uploaded contract is shown as pending`() = runTest(dispatcher) {
        val viewModel = RouteViewModel(
            FakeStudentRepository(
                dashboard = dashboard(RouteStageType.Contract),
                contractStatus = StudentDocumentStatus.UnderReview,
            ),
        )

        val state = viewModel.uiState.value
        assertEquals(StudentDocumentStatus.UnderReview, state.contractStatus)
        assertEquals(
            RoutePrimaryAction.ContractPending,
            state.stages.primaryAction(state.contractStatus, state.hasSelectedOffer, state.admissionState),
        )
    }

    @Test
    fun `confirmed contract opens academic performance`() = runTest(dispatcher) {
        val viewModel = RouteViewModel(
            FakeStudentRepository(
                dashboard = dashboard(RouteStageType.Education),
                contractStatus = StudentDocumentStatus.Accepted,
            ),
        )

        val state = viewModel.uiState.value
        assertEquals(
            RoutePrimaryAction.AcademicPerformance,
            state.stages.primaryAction(state.contractStatus, state.hasSelectedOffer, state.admissionState),
        )
    }

    @Test
    fun `contract stays locked before enrollment`() = runTest(dispatcher) {
        val viewModel = RouteViewModel(
            FakeStudentRepository(
                dashboard = dashboard(RouteStageType.Contract),
                contractStatus = StudentDocumentStatus.AwaitingUpload,
                admissionState = AdmissionState.Recommended,
            ),
        )

        val state = viewModel.uiState.value
        assertEquals(
            RoutePrimaryAction.ContractLocked,
            state.stages.primaryAction(state.contractStatus, state.hasSelectedOffer, state.admissionState),
        )
    }

    private class FakeStudentRepository(
        private val dashboard: StudentDashboard,
        private val contractStatus: StudentDocumentStatus,
        private val admissionState: AdmissionState = AdmissionState.Enrolled,
    ) : StudentRepository {
        override suspend fun loadDashboard() = dashboard
        override suspend fun loadDocuments() = listOf(
            StudentDocument(
                id = "contract",
                type = StudentDocumentType.TargetContract,
                title = "Договор",
                description = "",
                status = contractStatus,
            ),
        )
        override suspend fun uploadContract(document: StudentDocumentUpload) = Unit
        override suspend fun downloadDocument(document: StudentDocument) = error("Not used")
        override suspend fun loadTargetOfferSelection() = TargetOfferSelection(offer(), "selected")
        override suspend fun loadAdmissionStatus() = admissionStatus(admissionState)
        override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog = error("Not used")
        override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection = error("Not used")
        override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus = error("Not used")
        override suspend fun refreshAdmissionStatus(): AdmissionStatus = error("Not used")
        override suspend fun completeCareerGuidance() = error("Not used")
        override suspend fun logout() = error("Not used")
    }

    private companion object {
        fun dashboard(currentStage: RouteStageType) = StudentDashboard(
            routeProgressPercent = 40,
            routeStages = listOf(RouteStage(currentStage.name, currentStage, currentStage.name, RouteStageStatus.Current)),
            universities = emptyList(),
            profile = StudentProfile("Студент", "С", "Студент", 0),
        )

        fun offer() = TargetOffer(
            id = "offer",
            directionCode = null,
            directionName = null,
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
            officialApplicationUrl = "https://example.com",
            sourceUrl = "https://example.com",
        )

        fun admissionStatus(state: AdmissionState) = AdmissionStatus(
            applicantCodeMasked = "12**56",
            state = state,
            directionCode = null,
            directionName = null,
            profile = null,
            quota = "target",
            sponsorName = null,
            totalScore = null,
            consentToEnroll = null,
            mainPriority = null,
            highestPassingPriority = null,
            sourceKind = null,
            sourceUrl = null,
            checkedAt = null,
            isStale = false,
            warning = null,
        )
    }
}
