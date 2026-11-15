package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pararam2006.tseletrack.domain.events.EmployerEvent
import com.pararam2006.tseletrack.domain.events.EmployerEventFormat
import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.EmployerEventType
import com.pararam2006.tseletrack.ui.events.StudentEmployerEventsScreen
import com.pararam2006.tseletrack.ui.events.StudentEmployerEventsUiState
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EmployerEventsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun registrationActionPassesEventId() {
        var selectedEventId: String? = null
        composeRule.setContent {
            TseletrackTheme {
                StudentEmployerEventsScreen(
                    state = StudentEmployerEventsUiState(
                        organizationName = "АО «Сплав»",
                        events = listOf(summary()),
                        isLoading = false,
                    ),
                    onBack = {},
                    onRetry = {},
                    onToggleRegistration = { selectedEventId = it },
                )
            }
        }

        composeRule.onNodeWithText("Записаться").performClick()

        assertEquals("event-1", selectedEventId)
    }

    @Test
    fun loadErrorKeepsBackScreenAndOffersRetry() {
        var retried = false
        composeRule.setContent {
            TseletrackTheme {
                StudentEmployerEventsScreen(
                    state = StudentEmployerEventsUiState(
                        organizationName = "АО «Сплав»",
                        isLoading = false,
                        errorMessage = "Не удалось загрузить мероприятия",
                    ),
                    onBack = {},
                    onRetry = { retried = true },
                    onToggleRegistration = {},
                )
            }
        }

        composeRule.onNodeWithText("Мероприятия").assertIsDisplayed()
        composeRule.onNodeWithText("Не удалось загрузить мероприятия").assertIsDisplayed()
        composeRule.onNodeWithText("Повторить").performClick()

        assertTrue(retried)
    }

    private fun summary() = EmployerEventSummary(
        event = EmployerEvent(
            id = "event-1",
            organizationName = "АО «Сплав»",
            title = "Экскурсия",
            type = EmployerEventType.Excursion,
            format = EmployerEventFormat.Offline,
            startsAt = LocalDateTime.of(2026, 11, 21, 11, 0),
            location = "Тула",
            description = "Знакомство с предприятием",
            capacity = 30,
        ),
        registeredCount = 18,
        isRegistered = false,
    )
}
