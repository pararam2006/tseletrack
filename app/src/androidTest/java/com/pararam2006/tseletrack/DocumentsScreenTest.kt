package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.ui.student.DocumentsScreen
import com.pararam2006.tseletrack.ui.student.DocumentsUiState
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DocumentsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun awaitingContractCanOpenPdfPicker() {
        var pickerOpened = false
        setScreen(
            status = StudentDocumentStatus.AwaitingUpload,
            onUpload = { pickerOpened = true },
        )

        composeRule.onNodeWithText("Выбрать PDF договора")
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle { assertTrue(pickerOpened) }
    }

    @Test
    fun uploadedContractShowsReviewStatusAndFileName() {
        setScreen(StudentDocumentStatus.UnderReview, fileName = "contract.pdf")

        composeRule.onNodeWithText("На проверке в ТулГУ").assertIsDisplayed()
        composeRule.onNodeWithText("contract.pdf").assertIsDisplayed()
        composeRule.onNodeWithText("Заменить PDF").assertIsDisplayed()
    }

    @Test
    fun uploadedContractCanBeOpened() {
        var documentOpened = false
        setScreen(
            status = StudentDocumentStatus.UnderReview,
            fileName = "contract.pdf",
            downloadUrl = "demo://contract",
            onOpen = { documentOpened = true },
        )

        composeRule.onNodeWithText("Открыть PDF").performClick()

        composeRule.runOnIdle { assertTrue(documentOpened) }
    }

    @Test
    fun acceptedContractCanBeOpened() {
        var documentOpened = false
        setScreen(
            status = StudentDocumentStatus.Accepted,
            fileName = "confirmed-contract.pdf",
            downloadUrl = "/api/documents/1/content",
            onOpen = { documentOpened = true },
        )

        composeRule.onNodeWithText("Подтверждён вузом").assertIsDisplayed()
        composeRule.onNodeWithText("Открыть PDF").performClick()

        composeRule.runOnIdle { assertTrue(documentOpened) }
    }

    private fun setScreen(
        status: StudentDocumentStatus,
        fileName: String? = null,
        downloadUrl: String? = null,
        onUpload: () -> Unit = {},
        onOpen: () -> Unit = {},
    ) {
        composeRule.setContent {
            TseletrackTheme {
                DocumentsScreen(
                    state = DocumentsUiState(
                        isLoading = false,
                        documents = listOf(
                            StudentDocument(
                                id = "contract",
                                type = StudentDocumentType.TargetContract,
                                title = "Договор о целевом обучении",
                                description = "Описание",
                                status = status,
                                fileName = fileName,
                                downloadUrl = downloadUrl,
                            ),
                        ),
                    ),
                    onUploadContract = onUpload,
                    onOpenDocument = { onOpen() },
                )
            }
        }
    }
}
