package com.pararam2006.tseletrack.ui.role

import com.pararam2006.tseletrack.data.company.CompanyRepository
import com.pararam2006.tseletrack.data.university.UniversityRepository
import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.domain.role.CandidateItem
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard
import com.pararam2006.tseletrack.domain.role.ContractQueueItem
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.student.DownloadedDocument
import com.pararam2006.tseletrack.ui.company.CompanyViewModel
import com.pararam2006.tseletrack.ui.university.UniversityViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoleActionRaceTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `university handles only one contract action at a time`() = runTest(dispatcher) {
        val repository = DelayedUniversityRepository()
        val viewModel = UniversityViewModel(repository)
        advanceUntilIdle()

        viewModel.confirmContract(CONTRACT_ID)
        viewModel.declineContract(CONTRACT_ID)
        runCurrent()

        assertEquals(CONTRACT_ID, viewModel.uiState.value.processingContractId)
        assertEquals(1, repository.confirmCalls)
        assertEquals(0, repository.declineCalls)

        repository.actionGate.complete(Unit)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.processingContractId)
        assertTrue(viewModel.uiState.value.dashboard?.queue.orEmpty().isEmpty())
    }

    @Test
    fun `company handles only one candidate transition at a time`() = runTest(dispatcher) {
        val repository = DelayedCompanyRepository()
        val viewModel = CompanyViewModel(repository)
        advanceUntilIdle()

        viewModel.advanceCandidate(CANDIDATE_ID, CandidateStatus.Employment)
        viewModel.declineCandidate(CANDIDATE_ID)
        runCurrent()

        assertEquals(CANDIDATE_ID, viewModel.uiState.value.processingCandidateId)
        assertEquals(1, repository.advanceCalls)
        assertEquals(0, repository.declineCalls)

        repository.actionGate.complete(Unit)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.processingCandidateId)
        assertEquals(
            CandidateStatus.Employment,
            viewModel.uiState.value.dashboard?.candidates?.single()?.status,
        )
    }

    private class DelayedUniversityRepository : UniversityRepository {
        val actionGate = CompletableDeferred<Unit>()
        var confirmCalls = 0
        var declineCalls = 0
        private var dashboard = universityDashboard()

        override suspend fun loadDashboard(): UniversityDashboard = dashboard

        override suspend fun confirmContract(contractId: String) {
            confirmCalls++
            actionGate.await()
            dashboard = dashboard.copy(queue = emptyList(), queueCount = 0)
        }

        override suspend fun declineContract(contractId: String) {
            declineCalls++
        }

        override suspend fun downloadDocument(
            documentId: String,
            fileName: String,
        ): DownloadedDocument = error("Not used")

        override suspend fun logout() = Unit
    }

    private class DelayedCompanyRepository : CompanyRepository {
        val actionGate = CompletableDeferred<Unit>()
        var advanceCalls = 0
        var declineCalls = 0
        private var dashboard = companyDashboard()

        override suspend fun loadDashboard(): CompanyDashboard = dashboard

        override suspend fun updateCandidateStatus(
            candidateId: String,
            status: CandidateStatus,
        ) {
            advanceCalls++
            actionGate.await()
            dashboard = dashboard.copy(
                candidates = dashboard.candidates.map { candidate ->
                    if (candidate.id == candidateId) candidate.copy(status = status) else candidate
                },
            )
        }

        override suspend fun declineCandidate(candidateId: String) {
            declineCalls++
        }

        override suspend fun logout() = Unit
    }

    private companion object {
        const val CONTRACT_ID = "contract"
        const val CANDIDATE_ID = "candidate"

        val analytics = AnalyticsDashboard(
            candidates = 1,
            employedPercent = 0,
            inEducation = 1,
            waitingDecision = 0,
            universityValues = emptyList(),
        )

        fun universityDashboard() = UniversityDashboard(
            studentCount = 1,
            queueCount = 1,
            availablePlaces = 1,
            queue = listOf(
                ContractQueueItem(
                    id = CONTRACT_ID,
                    studentName = "Студент",
                    avatarLetter = "С",
                    signedAt = "Сегодня",
                ),
            ),
            students = emptyList(),
            analytics = analytics,
            profile = RoleProfile("ТулГУ", "Т", "Вуз"),
        )

        fun companyDashboard() = CompanyDashboard(
            candidates = listOf(
                CandidateItem(
                    id = CANDIDATE_ID,
                    name = "Студент",
                    avatarLetter = "С",
                    university = "ТулГУ",
                    stage = "Обучение",
                    progressPercent = 50,
                    status = CandidateStatus.Education,
                ),
            ),
            analytics = analytics,
            profile = RoleProfile("Предприятие", "П", "Работодатель"),
        )
    }
}
