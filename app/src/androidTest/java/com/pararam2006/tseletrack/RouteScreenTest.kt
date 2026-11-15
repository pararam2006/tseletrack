package com.pararam2006.tseletrack

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.ui.student.RouteScreen
import com.pararam2006.tseletrack.ui.theme.TseletrackTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RouteScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun careerGuidanceActionHasCorrectLabelAndCallback() {
        var careerGuidanceOpened = false
        setRouteScreen(
            currentStage = RouteStageType.CareerGuidance,
            onCareerGuidanceClick = { careerGuidanceOpened = true },
        )

        composeRule.onNodeWithText("Пройти профориентацию")
            .assertIsDisplayed()
            .performClick()

        assertTrue(careerGuidanceOpened)
    }

    @Test
    fun educationActionOpensAcademicPerformance() {
        var academicPerformanceOpened = false
        setRouteScreen(
            currentStage = RouteStageType.Education,
            onAcademicPerformanceClick = { academicPerformanceOpened = true },
        )

        composeRule.onNodeWithText("Посмотреть успеваемость")
            .assertIsDisplayed()
            .performClick()

        assertTrue(academicPerformanceOpened)
    }

    @Test
    fun signedContractShowsDisabledPendingAction() {
        setRouteScreen(
            currentStage = RouteStageType.Contract,
            contractStatus = StudentDocumentStatus.UnderReview,
        )

        composeRule.onNodeWithText("Договор на проверке в ТулГУ")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun contractIsLockedBeforeEnrollment() {
        setRouteScreen(
            currentStage = RouteStageType.Contract,
            admissionState = AdmissionState.Recommended,
        )

        composeRule.onNodeWithText("Договор доступен после зачисления")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    private fun setRouteScreen(
        currentStage: RouteStageType,
        contractStatus: StudentDocumentStatus = StudentDocumentStatus.AwaitingUpload,
        admissionState: AdmissionState = AdmissionState.Enrolled,
        onCareerGuidanceClick: () -> Unit = {},
        onDocumentsClick: () -> Unit = {},
        onAcademicPerformanceClick: () -> Unit = {},
    ) {
        composeRule.setContent {
            TseletrackTheme {
                RouteScreen(
                    isLoading = false,
                    contractStatus = contractStatus,
                    hasSelectedOffer = true,
                    admissionState = admissionState,
                    progressPercent = 40,
                    stages = listOf(
                        RouteStage(
                            id = currentStage.name,
                            type = currentStage,
                            title = currentStage.name,
                            status = RouteStageStatus.Current,
                        ),
                    ),
                    onCareerGuidanceClick = onCareerGuidanceClick,
                    onUniversityChoiceClick = {},
                    onDocumentsClick = onDocumentsClick,
                    onAcademicPerformanceClick = onAcademicPerformanceClick,
                )
            }
        }
    }
}
