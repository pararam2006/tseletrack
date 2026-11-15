package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import com.pararam2006.tseletrack.domain.student.AcademicResult
import com.pararam2006.tseletrack.ui.student.AcademicPerformanceScreen
import com.pararam2006.tseletrack.ui.student.AcademicPerformanceUiState
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AcademicPerformanceScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun linkedPerformanceShowsSemesterAndGrades() {
        setScreen(AcademicPerformanceUiState(isLoading = false, performance = performance))

        composeRule.onNodeWithText("Электронная зачётная книжка ТулГУ").assertIsDisplayed()
        composeRule.onNodeWithText("2 семестр").assertIsDisplayed()
        composeRule.onNodeWithText("Физика").assertIsDisplayed()
        composeRule.onNodeWithText("Хорошо").assertIsDisplayed()
        composeRule.onNodeWithText("Демоданные").assertIsDisplayed()
    }

    @Test
    fun semestersAreDisplayedFromFirstToLast() {
        setScreen(
            AcademicPerformanceUiState(
                isLoading = false,
                performance = performance.copy(
                    results = listOf(
                        AcademicResult(2, "Физика", "78", "Хорошо", null, null, false),
                        AcademicResult(1, "Математика", "90", "Отлично", null, null, false),
                    ),
                ),
            ),
        )

        val firstSemesterTop = composeRule.onAllNodesWithText("1 семестр")
            .fetchSemanticsNodes().single().boundsInRoot.top
        val secondSemesterTop = composeRule.onAllNodesWithText("2 семестр")
            .fetchSemanticsNodes().single().boundsInRoot.top

        assertTrue(firstSemesterTop < secondSemesterTop)
    }

    @Test
    fun emptyTrackingFormForwardsInputAndConnectAction() {
        var recordBook = ""
        var group = ""
        var connected = false
        composeRule.setContent {
            var state by remember {
                mutableStateOf(AcademicPerformanceUiState(isLoading = false))
            }
            TseletrackTheme {
                AcademicPerformanceScreen(
                    state = state,
                    onRecordBookNumberChange = {
                        recordBook = it
                        state = state.copy(recordBookNumber = it)
                    },
                    onGroupTitleChange = {
                        group = it
                        state = state.copy(groupTitle = it)
                    },
                    onConnect = { connected = true },
                    onRefresh = {},
                    onEditTracking = {},
                    onCancelEditing = {},
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Номер зачётной книжки")
            .performTextInput("12345678")
        composeRule.onNodeWithText("Номер учебной группы")
            .performTextInput("ИВТ-221")
        composeRule.onNodeWithText("Загрузить успеваемость").performClick()

        composeRule.runOnIdle {
            assertEquals("12345678", recordBook)
            assertEquals("ИВТ-221", group)
            assertTrue(connected)
        }
    }

    private fun setScreen(
        state: AcademicPerformanceUiState,
        onRecordBookNumberChange: (String) -> Unit = {},
        onGroupTitleChange: (String) -> Unit = {},
        onConnect: () -> Unit = {},
    ) {
        composeRule.setContent {
            TseletrackTheme {
                AcademicPerformanceScreen(
                    state = state,
                    onRecordBookNumberChange = onRecordBookNumberChange,
                    onGroupTitleChange = onGroupTitleChange,
                    onConnect = onConnect,
                    onRefresh = {},
                    onEditTracking = {},
                    onCancelEditing = {},
                    onBack = {},
                )
            }
        }
    }

    private companion object {
        val performance = AcademicPerformance(
            recordBookNumberMasked = "12••••78",
            groupTitle = "ИВТ-221",
            results = listOf(
                AcademicResult(2, "Физика", "78", "Хорошо", null, null, false),
            ),
            fetchedAt = "Сегодня",
            isStale = false,
            warning = null,
            sourceUrl = "https://tulsu.ru/progress/",
            isDemo = true,
        )
    }
}
