package com.pararam2006.tseletrack

import android.app.KeyguardManager
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.pararam2006.tseletrack.ui.student.StudentRoot
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assume.assumeFalse

class StudentNavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun studentTabsNavigateAndRestoreTheirState() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        assumeFalse("UI test requires an unlocked device", keyguardManager.isDeviceLocked)

        composeRule.setContent {
            TseletrackTheme {
                StudentRoot(onLogout = {})
            }
        }

        composeRule.onNodeWithText("Мой образовательный маршрут").assertIsDisplayed()

        composeRule.onNodeWithTag("student_tab_tasks").performClick()
        composeRule.onNodeWithText("Загрузить паспортные данные").performClick()
        composeRule.onNodeWithText("Загрузить паспортные данные").assertIsOn()

        composeRule.onNodeWithTag("student_tab_universities").performClick()
        composeRule.onNodeWithText("Поиск по названию").assertIsDisplayed()

        composeRule.onNodeWithTag("student_tab_tasks").performClick()
        composeRule.onNodeWithText("Загрузить паспортные данные").assertIsOn()

        composeRule.onNodeWithTag("student_tab_profile").performClick()
        composeRule.onNodeWithText("Студент А.").assertIsDisplayed()
    }
}
