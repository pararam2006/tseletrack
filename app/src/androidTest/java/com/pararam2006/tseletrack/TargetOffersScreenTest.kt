package com.pararam2006.tseletrack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.ui.student.TargetOffersScreen
import com.pararam2006.tseletrack.ui.student.TargetOffersUiState
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Rule
import org.junit.Assert.assertEquals
import org.junit.Test

class TargetOffersScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun savedOfferCollapsesCatalogUntilUserRequestsOtherOffers() {
        val selectedOffer = offer("selected", "Строительство")
        val anotherOffer = offer("another", "Прикладная информатика")
        var state by mutableStateOf(
            TargetOffersUiState(
                isLoading = false,
                offers = listOf(selectedOffer, anotherOffer),
                selectedOffer = selectedOffer,
                isCatalogExpanded = false,
            ),
        )

        composeRule.setContent {
            TseletrackTheme {
                TargetOffersScreen(
                    state = state,
                    onSearchQueryChange = {},
                    onRefreshCatalog = {},
                    onSaveOfferToRoute = {},
                    onShowCatalog = { state = state.copy(isCatalogExpanded = true) },
                    onApplicantCodeChange = {},
                    onSaveApplicantCode = {},
                    onRefreshAdmissionStatus = {},
                    onOpenEmployerEvents = {},
                )
            }
        }

        composeRule.onNodeWithText("Сохранённое предложение").assertIsDisplayed()
        composeRule.onNodeWithText("Другие предложения").assertDoesNotExist()
        composeRule.onNodeWithText("Прикладная информатика", substring = true).assertDoesNotExist()

        composeRule.onNodeWithText("Смотреть другие предложения").performClick()

        composeRule.onNodeWithText("Другие предложения").assertIsDisplayed()
        composeRule.onNodeWithText("Направление, заказчик или место работы").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(
            hasText("Прикладная информатика", substring = true),
        )
        composeRule.onNodeWithText("Прикладная информатика", substring = true).assertIsDisplayed()
    }

    @Test
    fun employerEventsActionPassesOfferOrganization() {
        val selectedOffer = offer(
            id = "selected",
            directionName = "Строительство",
            proposerName = "АО «Сплав»",
        )
        var openedOrganization: String? = null

        composeRule.setContent {
            TseletrackTheme {
                TargetOffersScreen(
                    state = TargetOffersUiState(
                        isLoading = false,
                        offers = listOf(selectedOffer),
                        selectedOffer = selectedOffer,
                        isCatalogExpanded = false,
                    ),
                    onSearchQueryChange = {},
                    onRefreshCatalog = {},
                    onSaveOfferToRoute = {},
                    onShowCatalog = {},
                    onApplicantCodeChange = {},
                    onSaveApplicantCode = {},
                    onRefreshAdmissionStatus = {},
                    onOpenEmployerEvents = { openedOrganization = it },
                )
            }
        }

        composeRule.onNode(hasScrollAction()).performScrollToNode(
            hasText("Мероприятия работодателя"),
        )
        composeRule.onNodeWithText("Мероприятия работодателя").performClick()

        assertEquals("АО «Сплав»", openedOrganization)
    }

    private fun offer(
        id: String,
        directionName: String,
        proposerName: String? = null,
    ) = TargetOffer(
        id = id,
        directionCode = null,
        directionName = directionName,
        profile = null,
        programName = null,
        programLevelName = null,
        educationFormName = null,
        universityName = "ТулГУ",
        proposerName = proposerName,
        workplaceAddress = null,
        applicationDeadline = null,
        requestCount = null,
        requestLinkType = null,
        officialApplicationUrl = "https://trudvsem.ru/offer/$id",
        sourceUrl = "https://trudvsem.ru/offer/$id",
    )
}
