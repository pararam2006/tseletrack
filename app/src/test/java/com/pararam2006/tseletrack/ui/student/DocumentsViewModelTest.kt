package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.student.StubStudentRepository
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `valid PDF is sent for university review`() = runTest(dispatcher) {
        val store = DemoPlatformStore().apply { admissionState = AdmissionState.Enrolled }
        val viewModel = DocumentsViewModel(StubStudentRepository(store))

        viewModel.uploadContract(
            StudentDocumentUpload("Целевой договор.pdf", "application/pdf", 2048),
        )

        val state = viewModel.uiState.value
        val contract = state.documents.single { it.type == StudentDocumentType.TargetContract }
        assertEquals(StudentDocumentStatus.UnderReview, contract.status)
        assertEquals("Целевой договор.pdf", contract.fileName)
        assertEquals("Договор отправлен в ТулГУ на проверку", state.actionMessage)
    }

    @Test
    fun `invalid file does not change contract status`() = runTest(dispatcher) {
        val store = DemoPlatformStore().apply { admissionState = AdmissionState.Enrolled }
        val viewModel = DocumentsViewModel(StubStudentRepository(store))

        viewModel.uploadContract(StudentDocumentUpload("contract.docx", null, 2048))

        assertEquals("Выберите договор в формате PDF", viewModel.uiState.value.actionMessage)
        assertEquals(StudentDocumentStatus.AwaitingUpload, store.contractStatus)
    }

    @Test
    fun `PDF larger than limit is rejected`() {
        assertEquals(
            "Размер файла не должен превышать 10 МБ",
            validateContractUpload(
                StudentDocumentUpload(
                    fileName = "contract.pdf",
                    mimeType = "application/pdf",
                    sizeBytes = 10L * 1024 * 1024 + 1,
                ),
            ),
        )
        assertNull(
            validateContractUpload(
                StudentDocumentUpload("contract.PDF", null, 10L * 1024 * 1024),
            ),
        )
    }

    @Test
    fun `uploaded demo document can be downloaded`() = runTest(dispatcher) {
        val store = DemoPlatformStore().apply {
            admissionState = AdmissionState.Enrolled
            contractStatus = StudentDocumentStatus.UnderReview
            contractFileName = "contract.pdf"
        }
        val viewModel = DocumentsViewModel(StubStudentRepository(store))
        val downloaded = async { viewModel.documentsToOpen.first() }

        viewModel.openDocument("target-contract")

        assertEquals("contract.pdf", downloaded.await().fileName)
        assertEquals("application/pdf", downloaded.await().contentType)
    }

    @Test
    fun `official enrollment order can be downloaded in demo`() = runTest(dispatcher) {
        val store = DemoPlatformStore().apply { admissionState = AdmissionState.Enrolled }
        val viewModel = DocumentsViewModel(StubStudentRepository(store))
        val downloaded = async { viewModel.documentsToOpen.first() }

        viewModel.openDocument("enrollment-order")

        assertEquals("Приказ о зачислении.pdf", downloaded.await().fileName)
        assertEquals("application/pdf", downloaded.await().contentType)
    }
}
