package com.pararam2006.tseletrack.ui.student.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pararam2006.tseletrack.ui.student.ProfileScreen
import com.pararam2006.tseletrack.ui.student.ProfileViewModel
import com.pararam2006.tseletrack.ui.student.RouteScreen
import com.pararam2006.tseletrack.ui.student.RouteViewModel
import com.pararam2006.tseletrack.ui.student.DocumentsScreen
import com.pararam2006.tseletrack.ui.student.DocumentsViewModel
import com.pararam2006.tseletrack.ui.student.AcademicPerformanceScreen
import com.pararam2006.tseletrack.ui.student.AcademicPerformanceViewModel
import com.pararam2006.tseletrack.ui.student.TargetOffersScreen
import com.pararam2006.tseletrack.ui.student.TargetOffersViewModel
import com.pararam2006.tseletrack.ui.career.navigation.CareerGuidanceNavGraph
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.common.AppErrorScreen
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.ui.student.readDocumentUpload
import com.pararam2006.tseletrack.ui.common.openDownloadedDocument
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun StudentNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routeViewModel = koinViewModel<RouteViewModel>()
    NavHost(
        navController = navController,
        startDestination = StudentRoute.Roadmap,
        modifier = modifier,
    ) {
        composable<StudentRoute.Roadmap> {
            val state by routeViewModel.uiState.collectAsStateWithLifecycle()
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.route_title), error, routeViewModel::retry)
            } else {
                RouteScreen(
                    isLoading = state.isLoading,
                    contractStatus = state.contractStatus,
                    hasSelectedOffer = state.hasSelectedOffer,
                    admissionState = state.admissionState,
                    progressPercent = state.progressPercent,
                    stages = state.stages,
                    onCareerGuidanceClick = {
                        navController.navigate(StudentRoute.CareerGuidance)
                    },
                    onUniversityChoiceClick = {
                        navController.navigateToStudentRoute(StudentRoute.TargetOffers)
                    },
                    onDocumentsClick = {
                        navController.navigateToStudentRoute(StudentRoute.Documents)
                    },
                    onAcademicPerformanceClick = {
                        navController.navigate(StudentRoute.AcademicPerformance)
                    },
                )
            }
        }

        composable<StudentRoute.CareerGuidance> {
            CareerGuidanceNavGraph(
                isAuthenticated = true,
                onBack = navController::popBackStack,
                onRequestAuth = {},
                onFinished = {
                    routeViewModel.refresh()
                    navController.popBackStack()
                },
            )
        }

        composable<StudentRoute.TargetOffers> {
            val viewModel = koinViewModel<TargetOffersViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(state.selectedOffer?.id, state.admissionStatus?.checkedAt) {
                if (state.selectedOffer != null || state.admissionStatus?.checkedAt != null) {
                    routeViewModel.refresh()
                }
            }
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.target_offers_title), error, viewModel::retry)
            } else {
                TargetOffersScreen(
                    state = state,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onRefreshCatalog = viewModel::refreshCatalog,
                    onSaveOfferToRoute = viewModel::saveOfferToRoute,
                    onShowCatalog = viewModel::showCatalog,
                    onApplicantCodeChange = viewModel::updateApplicantCode,
                    onSaveApplicantCode = viewModel::saveApplicantCode,
                    onRefreshAdmissionStatus = viewModel::refreshAdmissionStatus,
                )
            }
        }

        composable<StudentRoute.Documents> {
            val viewModel = koinViewModel<DocumentsViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val contractStatus = state.documents
                .firstOrNull { it.type == StudentDocumentType.TargetContract }
                ?.status
            LaunchedEffect(contractStatus) {
                if (contractStatus != null) routeViewModel.refresh()
            }
            LaunchedEffect(viewModel) {
                viewModel.refresh()
                viewModel.documentsToOpen.collect { document ->
                    if (!context.openDownloadedDocument(document)) {
                        viewModel.documentOpenFailed()
                    }
                }
            }
            val documentPicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument(),
            ) { uri ->
                if (uri != null) {
                    viewModel.uploadContract(context.contentResolver.readDocumentUpload(uri))
                }
            }
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.documents_title), error, viewModel::retry)
            } else {
                DocumentsScreen(
                    state = state,
                    onUploadContract = { documentPicker.launch(arrayOf("application/pdf")) },
                    onOpenDocument = viewModel::openDocument,
                )
            }
        }

        composable<StudentRoute.AcademicPerformance> {
            val viewModel = koinViewModel<AcademicPerformanceViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.reloadForActiveUser()
            }
            AcademicPerformanceScreen(
                state = state,
                onRecordBookNumberChange = viewModel::updateRecordBookNumber,
                onGroupTitleChange = viewModel::updateGroupTitle,
                onConnect = viewModel::connect,
                onRefresh = viewModel::refresh,
                onEditTracking = viewModel::editTracking,
                onCancelEditing = viewModel::cancelEditing,
                onBack = navController::popBackStack,
            )
        }

        composable<StudentRoute.Profile> {
            val viewModel = koinViewModel<ProfileViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.refresh()
            }
            val error = state.errorMessage
            if (error != null) {
                AppErrorScreen(stringResource(R.string.profile_title), error, viewModel::retry)
            } else {
                ProfileScreen(
                    isLoading = state.isLoading,
                    profile = state.profile,
                    isAcademicPerformanceAvailable = state.isAcademicPerformanceAvailable,
                    onDocumentsClick = {
                        navController.navigateToStudentRoute(StudentRoute.Documents)
                    },
                    onAcademicPerformanceClick = {
                        navController.navigate(StudentRoute.AcademicPerformance)
                    },
                    onLogout = onLogout,
                )
            }
        }
    }
}
