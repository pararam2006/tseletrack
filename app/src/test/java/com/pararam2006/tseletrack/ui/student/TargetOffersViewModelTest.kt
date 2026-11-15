package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TargetOffersViewModelTest {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial selection collapses catalog and may be absent from active offers`() = runTest(dispatcher) {
        val activeOffer = offer("active")
        val inactiveSelectedOffer = offer("inactive")
        val repository = FakeStudentRepository(
            catalog = catalog(activeOffer),
            selection = TargetOfferSelection(inactiveSelectedOffer, "selected_locally"),
        )

        val state = TargetOffersViewModel(repository).uiState.value

        assertFalse(state.isLoading)
        assertSame(inactiveSelectedOffer, state.selectedOffer)
        assertFalse(state.isCatalogExpanded)
        assertEquals(listOf(activeOffer), state.visibleOffers)
    }

    @Test
    fun `saving offer updates selection clears search and collapses catalog`() = runTest(dispatcher) {
        val selectedOffer = offer("selected")
        val repository = FakeStudentRepository(
            catalog = catalog(selectedOffer, offer("another")),
            selectionToReturn = TargetOfferSelection(selectedOffer, "selected_locally"),
            admissionStatus = admissionStatus(AdmissionState.Recommended),
        )
        val viewModel = TargetOffersViewModel(repository)
        viewModel.updateSearchQuery("строй")

        viewModel.saveOfferToRoute(selectedOffer.id)

        val state = viewModel.uiState.value
        assertEquals(selectedOffer.id, repository.chosenOfferId)
        assertSame(selectedOffer, state.selectedOffer)
        assertFalse(state.isCatalogExpanded)
        assertEquals("", state.searchQuery)
        assertNull(state.admissionStatus)
        assertEquals(TargetOffersMessage.OfferSavedToRoute, state.actionMessage)
    }

    @Test
    fun `show catalog expands it and clears old action message`() = runTest(dispatcher) {
        val selectedOffer = offer("selected")
        val viewModel = TargetOffersViewModel(
            FakeStudentRepository(
                catalog = catalog(selectedOffer),
                selection = TargetOfferSelection(selectedOffer, "selected_locally"),
                selectionToReturn = TargetOfferSelection(selectedOffer, "selected_locally"),
            ),
        )
        viewModel.saveOfferToRoute(selectedOffer.id)

        viewModel.showCatalog()

        assertTrue(viewModel.uiState.value.isCatalogExpanded)
        assertNull(viewModel.uiState.value.actionMessage)
    }

    @Test
    fun `refresh replaces active offers but preserves inactive selection`() = runTest(dispatcher) {
        val inactiveSelectedOffer = offer("inactive")
        val repository = FakeStudentRepository(
            catalog = catalog(offer("old")),
            refreshedCatalog = catalog(offer("new")),
            selection = TargetOfferSelection(inactiveSelectedOffer, "selected_locally"),
        )
        val viewModel = TargetOffersViewModel(repository)

        viewModel.refreshCatalog()

        val state = viewModel.uiState.value
        assertEquals(listOf(false, true), repository.catalogRequests)
        assertEquals(listOf("new"), state.offers.map(TargetOffer::id))
        assertSame(inactiveSelectedOffer, state.selectedOffer)
        assertEquals(TargetOffersMessage.CatalogUpdated, state.actionMessage)
        assertFalse(state.isRefreshingCatalog)
    }

    @Test
    fun `initial catalog failure exposes retryable screen error`() = runTest(dispatcher) {
        val repository = FakeStudentRepository(
            catalogError = IllegalStateException("sensitive internal detail"),
        )

        val state = TargetOffersViewModel(repository).uiState.value

        assertFalse(state.isLoading)
        assertEquals("Не удалось выполнить запрос. Попробуйте ещё раз.", state.errorMessage)
    }

    @Test
    fun `selection error keeps catalog open and exposes api message`() = runTest(dispatcher) {
        val repository = FakeStudentRepository(
            catalog = catalog(offer("one")),
            chooseError = ApiException(ApiErrorKind.Validation, "Предложение больше недоступно"),
        )
        val viewModel = TargetOffersViewModel(repository)

        viewModel.saveOfferToRoute("one")

        val state = viewModel.uiState.value
        assertNull(state.selectedOffer)
        assertTrue(state.isCatalogExpanded)
        assertNull(state.selectingOfferId)
        assertEquals(
            TargetOffersMessage.Error("Предложение больше недоступно"),
            state.actionMessage,
        )
    }

    @Test
    fun `refresh error keeps previous catalog and resets progress`() = runTest(dispatcher) {
        val existingOffer = offer("existing")
        val repository = FakeStudentRepository(
            catalog = catalog(existingOffer),
            refreshError = ApiException(ApiErrorKind.Network, "Источник временно недоступен"),
        )
        val viewModel = TargetOffersViewModel(repository)

        viewModel.refreshCatalog()

        val state = viewModel.uiState.value
        assertEquals(listOf(existingOffer), state.offers)
        assertFalse(state.isRefreshingCatalog)
        assertEquals(
            TargetOffersMessage.Error("Источник временно недоступен"),
            state.actionMessage,
        )
    }

    @Test
    fun `offer cannot change while applicant code is being saved`() = runTest(dispatcher) {
        val selected = offer("selected")
        val saveGate = CompletableDeferred<Unit>()
        val repository = FakeStudentRepository(
            catalog = catalog(selected, offer("another")),
            selection = TargetOfferSelection(selected, "selected_locally"),
            saveApplicantCodeGate = saveGate,
            savedAdmissionStatus = admissionStatus(AdmissionState.ApplicationFound),
        )
        val viewModel = TargetOffersViewModel(repository)
        viewModel.updateApplicantCode("123456")

        viewModel.saveApplicantCode()
        viewModel.saveOfferToRoute("another")

        assertTrue(viewModel.uiState.value.isSavingCode)
        assertFalse(viewModel.uiState.value.canSelectOffer)
        assertNull(repository.chosenOfferId)

        saveGate.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSavingCode)
        assertEquals("selected", viewModel.uiState.value.selectedOffer?.id)
        assertEquals(AdmissionState.ApplicationFound, viewModel.uiState.value.admissionStatus?.state)
    }

    @Test
    fun `offer and code save are blocked while admission status refreshes`() = runTest(dispatcher) {
        val selected = offer("selected")
        val refreshGate = CompletableDeferred<Unit>()
        val repository = FakeStudentRepository(
            catalog = catalog(selected, offer("another")),
            selection = TargetOfferSelection(selected, "selected_locally"),
            admissionStatus = admissionStatus(AdmissionState.ApplicationFound),
            refreshAdmissionStatusGate = refreshGate,
            refreshedAdmissionStatus = admissionStatus(AdmissionState.Recommended),
        )
        val viewModel = TargetOffersViewModel(repository)
        viewModel.updateApplicantCode("123456")

        viewModel.refreshAdmissionStatus()
        viewModel.saveOfferToRoute("another")
        viewModel.saveApplicantCode()

        val pendingState = viewModel.uiState.value
        assertTrue(pendingState.isRefreshingStatus)
        assertFalse(pendingState.canSelectOffer)
        assertFalse(pendingState.isSavingCode)
        assertNull(repository.chosenOfferId)

        refreshGate.complete(Unit)
        advanceUntilIdle()

        assertEquals(AdmissionState.Recommended, viewModel.uiState.value.admissionStatus?.state)
        assertTrue(viewModel.uiState.value.canSelectOffer)
    }

    private class FakeStudentRepository(
        private val catalog: TargetOfferCatalog = catalog(),
        private val refreshedCatalog: TargetOfferCatalog = catalog,
        private val selection: TargetOfferSelection? = null,
        private val selectionToReturn: TargetOfferSelection = TargetOfferSelection(offer("chosen"), "selected_locally"),
        private val catalogError: Exception? = null,
        private val refreshError: Exception? = null,
        private val chooseError: Exception? = null,
        private val admissionStatus: AdmissionStatus? = null,
        private val saveApplicantCodeGate: CompletableDeferred<Unit>? = null,
        private val savedAdmissionStatus: AdmissionStatus? = null,
        private val refreshAdmissionStatusGate: CompletableDeferred<Unit>? = null,
        private val refreshedAdmissionStatus: AdmissionStatus? = null,
    ) : StudentRepository {
        val catalogRequests = mutableListOf<Boolean>()
        var chosenOfferId: String? = null

        override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog {
            catalogRequests += forceRefresh
            (if (forceRefresh) refreshError else catalogError)?.let { throw it }
            return if (forceRefresh) refreshedCatalog else catalog
        }

        override suspend fun loadTargetOfferSelection() = selection

        override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection {
            chosenOfferId = offerId
            chooseError?.let { throw it }
            return selectionToReturn
        }

        override suspend fun loadAdmissionStatus(): AdmissionStatus? = admissionStatus
        override suspend fun loadDashboard(): StudentDashboard = error("Not used")
        override suspend fun loadDocuments() = error("Not used")
        override suspend fun uploadContract(
            document: com.pararam2006.tseletrack.domain.student.StudentDocumentUpload,
        ) = error("Not used")
        override suspend fun downloadDocument(
            document: com.pararam2006.tseletrack.domain.student.StudentDocument,
        ) = error("Not used")
        override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus {
            saveApplicantCodeGate?.await()
            return checkNotNull(savedAdmissionStatus)
        }

        override suspend fun refreshAdmissionStatus(): AdmissionStatus {
            refreshAdmissionStatusGate?.await()
            return checkNotNull(refreshedAdmissionStatus)
        }
        override suspend fun completeCareerGuidance() = error("Not used")
        override suspend fun logout() = error("Not used")
    }

    private companion object {
        fun catalog(vararg offers: TargetOffer) = TargetOfferCatalog(
            items = offers.toList(),
            isStale = false,
            warning = null,
        )

        fun offer(id: String) = TargetOffer(
            id = id,
            directionCode = "08.03.01",
            directionName = "Строительство $id",
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
            officialApplicationUrl = "https://trudvsem.ru/offer/$id",
            sourceUrl = "https://trudvsem.ru/offer/$id",
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
