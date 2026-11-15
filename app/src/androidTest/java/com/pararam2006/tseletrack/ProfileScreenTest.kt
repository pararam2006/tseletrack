package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.ui.student.ProfileScreen
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun profileShowsSystemNotificationsButNotTheme() {
        setProfile()

        composeRule.onNodeWithText("Тема оформления").assertDoesNotExist()
        composeRule.onNodeWithText("Уведомления").assertIsDisplayed()
        composeRule.onNodeWithText("Документы").assertIsDisplayed()
    }

    @Test
    fun documentsRowKeepsItsActionAfterRemovingTheme() {
        var documentsOpened = false
        setProfile(onDocumentsClick = { documentsOpened = true })

        composeRule.onNodeWithText("Документы").performClick()

        composeRule.runOnIdle {
            assertEquals(true, documentsOpened)
        }
    }

    @Test
    fun academicPerformanceIsShownOnlyDuringEducation() {
        setProfile(isAcademicPerformanceAvailable = true)

        composeRule.onNodeWithText("Успеваемость").assertIsDisplayed()
        composeRule.onNodeWithText("Открыть").assertIsDisplayed()
    }

    private fun setProfile(
        isAcademicPerformanceAvailable: Boolean = false,
        onDocumentsClick: () -> Unit = {},
    ) {
        composeRule.setContent {
            TseletrackTheme {
                ProfileScreen(
                    isLoading = false,
                    profile = StudentProfile(
                        displayName = "Иван Иванов",
                        avatarLetter = "И",
                        role = "Абитуриент",
                        documentCount = 2,
                    ),
                    isAcademicPerformanceAvailable = isAcademicPerformanceAvailable,
                    onDocumentsClick = onDocumentsClick,
                    onAcademicPerformanceClick = {},
                    onLogout = {},
                )
            }
        }
    }
}
