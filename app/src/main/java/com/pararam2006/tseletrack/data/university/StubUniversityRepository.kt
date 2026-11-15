package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.domain.role.ContractQueueItem
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.role.UniversityStudentItem
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

/** In-memory university data source. Replace with API calls when endpoints are available. */
class StubUniversityRepository(
    private val demoStore: DemoPlatformStore = DemoPlatformStore(),
) : UniversityRepository {
    override suspend fun loadDashboard(): UniversityDashboard {
        val queue = contractQueue()
        return UniversityDashboard(
            studentCount = 24 + if (demoStore.applicantCode == null) 0 else 1,
            queueCount = queue.size,
            availablePlaces = 120,
            queue = queue,
            students = demoStudent() + listOf(
                UniversityStudentItem("a", "Студент А.", "Договор", 33),
                UniversityStudentItem("b", "Студент Б.", "Обучение", 48),
                UniversityStudentItem("v", "Студент В.", "Трудоустройство", 83),
                UniversityStudentItem("g", "Студент Г.", "Выбор вуза", 17),
                UniversityStudentItem("d", "Студент Д.", "Трудоустроен", 100),
                UniversityStudentItem("e", "Студент Е.", "Обучение", 48),
            ),
            analytics = defaultAnalytics(),
            targetOfferMetrics = targetOfferMetrics(),
            profile = RoleProfile("ТулГУ", "Т", "Вуз"),
        )
    }

    override suspend fun confirmContract(contractId: String) {
        if (contractId == DEMO_CONTRACT_ID) {
            demoStore.contractStatus = StudentDocumentStatus.Accepted
        } else {
            demoStore.resolvedContractIds += contractId
        }
    }

    override suspend fun declineContract(contractId: String) {
        if (contractId == DEMO_CONTRACT_ID) {
            demoStore.contractStatus = StudentDocumentStatus.ChangesRequested
        } else {
            demoStore.resolvedContractIds += contractId
        }
    }

    override suspend fun downloadDocument(
        documentId: String,
        fileName: String,
    ): DownloadedDocument {
        check(documentId == DEMO_DOCUMENT_ID) { "В деморежиме файл доступен только у демо-абитуриента" }
        return DownloadedDocument(
            fileName = fileName,
            contentType = "application/pdf",
            bytes = DEMO_PDF,
        )
    }
    override suspend fun logout() = Unit

    private fun contractQueue(): List<ContractQueueItem> = buildList {
        if (demoStore.contractStatus == StudentDocumentStatus.UnderReview) {
            add(
                ContractQueueItem(
                    id = DEMO_CONTRACT_ID,
                    studentName = "Демо-абитуриент",
                    avatarLetter = "Д",
                    signedAt = "Загружен только что",
                    fileName = demoStore.contractFileName,
                    documentId = DEMO_DOCUMENT_ID,
                    documentSizeBytes = DEMO_PDF.size.toLong(),
                ),
            )
        }
        addAll(
            listOf(
                ContractQueueItem("a", "Студент А.", "А", "Загружен 14.11", "Договор_А.pdf"),
                ContractQueueItem("zh", "Студент Ж.", "Ж", "Загружен 13.11", "Договор_Ж.pdf"),
                ContractQueueItem("b", "Студент Б.", "Б", "Загружен 12.11", "Договор_Б.pdf"),
            ).filterNot { it.id in demoStore.resolvedContractIds },
        )
    }

    private fun demoStudent(): List<UniversityStudentItem> {
        if (demoStore.admissionState != com.pararam2006.tseletrack.domain.student.AdmissionState.Enrolled) {
            return emptyList()
        }
        val stage = when (demoStore.contractStatus) {
            StudentDocumentStatus.UnderReview,
            StudentDocumentStatus.ChangesRequested,
            StudentDocumentStatus.AwaitingUpload,
            -> "Договор"

            StudentDocumentStatus.Accepted -> "Обучение"
            StudentDocumentStatus.Unavailable,
            StudentDocumentStatus.Available,
            -> return emptyList()
        }
        return listOf(
            UniversityStudentItem(
                id = "demo-student",
                name = "Демо-абитуриент",
                stage = stage,
                progressPercent = if (stage == "Обучение") 60 else 40,
            ),
        )
    }

    private fun targetOfferMetrics(): List<TargetOfferMetric> = listOf(
        TargetOfferMetric(
            offerId = "338931",
            directionCode = "08.03.01",
            directionName = "Строительство",
            companyName = "АО «Сплав»",
            quotaPlaces = 5,
            applicationCount = 9 + demoStore.applicationIncrement("338931"),
            updatedAt = "Только что",
        ),
        TargetOfferMetric(
            offerId = "demo-kbp",
            directionCode = "09.03.01",
            directionName = "Информатика и вычислительная техника",
            companyName = "АО «КБП»",
            quotaPlaces = 20,
            applicationCount = 11 + demoStore.applicationIncrement("demo-kbp"),
            updatedAt = "Только что",
        ),
        TargetOfferMetric(
            offerId = "demo-azot",
            directionCode = "18.03.01",
            directionName = "Химическая технология",
            companyName = "АО «НАК „Азот“»",
            quotaPlaces = 5,
            applicationCount = demoStore.applicationIncrement("demo-azot"),
            updatedAt = "Только что",
        ),
    )

    private fun DemoPlatformStore.applicationIncrement(offerId: String): Int =
        if (isApplicationCountedFor(offerId)) 1 else 0

    private companion object {
        const val DEMO_CONTRACT_ID = "demo-contract"
        const val DEMO_DOCUMENT_ID = "demo-contract-document"
        val DEMO_PDF = "%PDF-1.4\n% Tseletrack demo contract\n%%EOF\n".encodeToByteArray()
    }
}

internal fun defaultAnalytics() = AnalyticsDashboard(
    candidates = 12,
    employedPercent = 64,
    inEducation = 8,
    waitingDecision = 3,
    universityValues = listOf("ТулГУ" to 12, "ТГПУ" to 8, "Вуз 3" to 5, "Вуз 4" to 3),
)
