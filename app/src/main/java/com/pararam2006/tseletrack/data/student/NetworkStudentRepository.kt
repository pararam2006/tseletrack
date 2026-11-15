package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AdmissionSourceKind
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload

class NetworkStudentRepository(
    private val api: TseletrackApi,
) : StudentRepository {
    override suspend fun loadDashboard(): StudentDashboard = api.studentDashboard().toDomain()

    override suspend fun loadDocuments(): List<StudentDocument> {
        val dashboard = loadDashboard()
        val admission = loadAdmissionStatus()
        val serverDocuments = api.studentDocuments().map { it.toDomain() }
        val contractStage = dashboard.routeStages.firstOrNull { it.type == RouteStageType.Contract }
        val serverContract = serverDocuments.firstOrNull {
            it.type == StudentDocumentType.TargetContract
        }
        val hasEnrollmentOrder = admission?.state == AdmissionState.Enrolled &&
            admission.sourceKind == AdmissionSourceKind.EnrollmentOrderList &&
            !admission.sourceUrl.isNullOrBlank()
        val localContractStatus = when {
            contractStage?.status == RouteStageStatus.Completed -> StudentDocumentStatus.Accepted
            admission?.state != AdmissionState.Enrolled -> StudentDocumentStatus.Unavailable
            else -> StudentDocumentStatus.AwaitingUpload
        }
        val contract = serverContract ?: StudentDocument(
            id = CONTRACT_DOCUMENT_ID,
            type = StudentDocumentType.TargetContract,
            title = "Договор о целевом обучении",
            description = if (localContractStatus == StudentDocumentStatus.Unavailable) {
                "Станет доступен после публикации приказа о зачислении."
            } else {
                "Загрузите подписанный PDF. Сотрудник ТулГУ проверит его и подтвердит этап."
            },
            status = localContractStatus,
        )
        return listOf(
            contract,
            StudentDocument(
                id = ENROLLMENT_ORDER_DOCUMENT_ID,
                type = StudentDocumentType.EnrollmentOrder,
                title = "Приказ о зачислении",
                description = "Официальный документ ТулГУ, найденный по коду поступающего.",
                status = if (hasEnrollmentOrder) {
                    StudentDocumentStatus.Available
                } else {
                    StudentDocumentStatus.Unavailable
                },
                fileName = if (hasEnrollmentOrder) {
                    "Приказ о зачислении.pdf"
                } else {
                    null
                },
                updatedAt = if (hasEnrollmentOrder) "Проверено по официальному списку" else null,
                downloadUrl = if (hasEnrollmentOrder) ENROLLMENT_ORDER_DOWNLOAD_URL else null,
            ),
        )
    }

    override suspend fun uploadContract(document: StudentDocumentUpload) {
        check(loadAdmissionStatus()?.state == AdmissionState.Enrolled) {
            "Договор станет доступен после приказа о зачислении"
        }
        val content = checkNotNull(document.content) { "Не удалось прочитать выбранный PDF" }
        val bytes = content.readBytes()
        api.uploadStudentContract(
            fileName = document.fileName,
            contentType = document.mimeType ?: "application/pdf",
            bytes = bytes,
        )
    }

    override suspend fun downloadDocument(document: StudentDocument) =
        api.downloadDocumentAt(
            downloadUrl = checkNotNull(document.downloadUrl) { "Документ недоступен" },
            fileName = checkNotNull(document.fileName) { "У документа нет имени файла" },
        )

    override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog =
        api.targetOffers(forceRefresh).toDomain()

    override suspend fun loadTargetOfferSelection(): TargetOfferSelection? =
        notFoundAsNull { api.targetOfferSelection().toDomain() }

    override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection =
        api.selectTargetOffer(offerId).toDomain()

    override suspend fun loadAdmissionStatus(): AdmissionStatus? =
        notFoundAsNull { api.admissionStatus().toDomain() }

    override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus =
        api.saveApplicantCode(applicantCode).toDomain()

    override suspend fun refreshAdmissionStatus(): AdmissionStatus {
        return api.refreshAdmissionStatus().toDomain()
    }

    override suspend fun completeCareerGuidance() {
        val careerStage = loadDashboard().routeStages.firstOrNull { stage ->
            stage.type == RouteStageType.CareerGuidance
        } ?: error("Этап профориентации не найден")
        if (careerStage.status == RouteStageStatus.Completed) return

        api.updateRouteStage(careerStage.id, COMPLETED_STATUS)
    }

    override suspend fun logout() {
        api.clearSession()
    }

    private suspend fun <T> notFoundAsNull(block: suspend () -> T): T? = try {
        block()
    } catch (error: ApiException) {
        if (error.kind == ApiErrorKind.NotFound) null else throw error
    }

    private companion object {
        const val COMPLETED_STATUS = "completed"
        const val CONTRACT_DOCUMENT_ID = "target-contract"
        const val ENROLLMENT_ORDER_DOCUMENT_ID = "enrollment-order"
        const val ENROLLMENT_ORDER_DOWNLOAD_URL =
            "/api/student/documents/enrollment-order/content"
    }
}
