package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.domain.student.AdmissionSourceKind
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.domain.student.DownloadedDocument
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
import com.pararam2006.tseletrack.domain.student.University
import java.time.LocalDateTime

/** Local, deterministic student journey used by the debug demo without network access. */
class StubStudentRepository(
    private val store: DemoPlatformStore = DemoPlatformStore(),
) : StudentRepository {

    override suspend fun loadDashboard(): StudentDashboard {
        val isEnrolled = store.admissionState == AdmissionState.Enrolled
        val targetOfferStatus = if (isEnrolled) {
            RouteStageStatus.Completed
        } else {
            RouteStageStatus.Current
        }
        val contractStatus = when {
            !isEnrolled -> RouteStageStatus.Waiting
            store.contractStatus == StudentDocumentStatus.Accepted -> RouteStageStatus.Completed
            else -> RouteStageStatus.Current
        }
        val educationStatus = when {
            store.contractStatus == StudentDocumentStatus.Accepted -> RouteStageStatus.Current
            isEnrolled -> RouteStageStatus.Waiting
            else -> RouteStageStatus.Future
        }

        return StudentDashboard(
            routeProgressPercent = when {
                store.contractStatus == StudentDocumentStatus.Accepted -> 60
                isEnrolled -> 40
                else -> 20
            },
            routeStages = listOf(
                RouteStage(
                    id = "career-guidance",
                    type = RouteStageType.CareerGuidance,
                    title = "Профориентация",
                    status = RouteStageStatus.Completed,
                ),
                RouteStage(
                    id = "target-offer",
                    type = RouteStageType.TargetOfferChoice,
                    title = "Поступление в ТулГУ",
                    status = targetOfferStatus,
                ),
                RouteStage(
                    id = "contract",
                    type = RouteStageType.Contract,
                    title = "Заключение договора",
                    status = contractStatus,
                ),
                RouteStage(
                    id = "education",
                    type = RouteStageType.Education,
                    title = "Обучение",
                    status = educationStatus,
                ),
                RouteStage(
                    id = "employment",
                    type = RouteStageType.Employment,
                    title = "Трудоустройство",
                    status = RouteStageStatus.Future,
                ),
            ),
            universities = listOf(
                University("tulsu", "ТулГУ", "Инженерные и цифровые направления", 92),
            ),
            profile = StudentProfile(
                displayName = "Демо-абитуриент",
                avatarLetter = "Д",
                role = "Абитуриент ТулГУ · деморежим",
                documentCount = buildList {
                    if (store.applicantCode != null) add("application")
                    if (isEnrolled) add("enrollment-order")
                    if (store.contractFileName != null) add("contract")
                }.size,
            ),
        )
    }

    override suspend fun loadDocuments(): List<StudentDocument> {
        val isEnrolled = store.admissionState == AdmissionState.Enrolled
        return listOf(
            StudentDocument(
                id = CONTRACT_DOCUMENT_ID,
                type = StudentDocumentType.TargetContract,
                title = "Договор о целевом обучении",
                description = if (isEnrolled) {
                    "Загрузите подписанный PDF. Сотрудник ТулГУ проверит его и подтвердит этап."
                } else {
                    "Станет доступен после публикации приказа о зачислении."
                },
                status = if (isEnrolled) store.contractStatus else StudentDocumentStatus.Unavailable,
                fileName = store.contractFileName,
                updatedAt = if (store.contractFileName == null) null else "Только что",
                downloadUrl = if (store.contractFileName == null) null else "demo://contract",
            ),
            StudentDocument(
                id = ENROLLMENT_ORDER_DOCUMENT_ID,
                type = StudentDocumentType.EnrollmentOrder,
                title = "Приказ о зачислении",
                description = "Официальный документ ТулГУ, найденный по коду поступающего.",
                status = if (isEnrolled) {
                    StudentDocumentStatus.Available
                } else {
                    StudentDocumentStatus.Unavailable
                },
                fileName = if (isEnrolled) "Приказ о зачислении.pdf" else null,
                updatedAt = if (isEnrolled) "Проверено по официальному списку" else null,
                downloadUrl = if (isEnrolled) "demo://enrollment-order" else null,
            ),
        )
    }

    override suspend fun uploadContract(document: StudentDocumentUpload) {
        check(store.admissionState == AdmissionState.Enrolled) {
            "Договор станет доступен после приказа о зачислении"
        }
        store.contractFileName = document.fileName
        store.contractStatus = StudentDocumentStatus.UnderReview
    }

    override suspend fun downloadDocument(document: StudentDocument): DownloadedDocument {
        when (document.type) {
            StudentDocumentType.TargetContract ->
                check(store.contractFileName != null) { "Документ ещё не загружен" }
            StudentDocumentType.EnrollmentOrder ->
                check(store.admissionState == AdmissionState.Enrolled) {
                    "Приказ о зачислении ещё не опубликован"
                }
            else -> error("Документ не найден")
        }
        return DownloadedDocument(
            fileName = checkNotNull(document.fileName),
            contentType = "application/pdf",
            bytes = DEMO_PDF,
        )
    }

    override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog =
        TargetOfferCatalog(
            items = offers,
            isStale = false,
            warning = "Деморежим: предложения и статусы хранятся локально.",
        )

    override suspend fun loadTargetOfferSelection(): TargetOfferSelection? =
        offers.firstOrNull { it.id == store.selectedOfferId }?.let {
            TargetOfferSelection(it, "selected_locally")
        }

    override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection {
        val offer = offers.firstOrNull { it.id == offerId }
            ?: error("Предложение не найдено")
        store.selectOffer(offer.id)
        return TargetOfferSelection(offer, "selected_locally")
    }

    override suspend fun loadAdmissionStatus(): AdmissionStatus? = store.admissionState?.let(::status)

    override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus {
        checkNotNull(store.selectedOfferId) { "Сначала сохраните целевое предложение" }
        store.applicantCode = applicantCode
        store.admissionState = AdmissionState.Unknown
        return status(AdmissionState.Unknown)
    }

    override suspend fun refreshAdmissionStatus(): AdmissionStatus {
        checkNotNull(store.applicantCode) { "Сначала укажите код поступающего" }
        val nextState = when (store.admissionState) {
            null,
            AdmissionState.Unknown,
            -> AdmissionState.ApplicationFound

            AdmissionState.ApplicationFound -> AdmissionState.Recommended
            AdmissionState.Recommended,
            AdmissionState.Enrolled,
            -> AdmissionState.Enrolled
        }
        store.admissionState = nextState
        if (nextState == AdmissionState.Enrolled && store.contractStatus == StudentDocumentStatus.Unavailable) {
            store.contractStatus = StudentDocumentStatus.AwaitingUpload
        }
        return status(nextState)
    }

    override suspend fun completeCareerGuidance() = Unit

    override suspend fun logout() = Unit

    private fun status(state: AdmissionState): AdmissionStatus {
        val offer = offers.first { it.id == store.selectedOfferId }
        val code = checkNotNull(store.applicantCode)
        val sourceKind = when (state) {
            AdmissionState.Enrolled -> AdmissionSourceKind.EnrollmentOrderList
            AdmissionState.ApplicationFound,
            AdmissionState.Recommended,
            -> AdmissionSourceKind.DailyList

            AdmissionState.Unknown -> null
        }
        return AdmissionStatus(
            applicantCodeMasked = code.maskApplicantCode(),
            state = state,
            directionCode = offer.directionCode,
            directionName = offer.directionName,
            profile = offer.profile,
            quota = "target",
            sponsorName = offer.proposerName,
            totalScore = if (state == AdmissionState.Unknown) null else 241,
            consentToEnroll = state == AdmissionState.Recommended || state == AdmissionState.Enrolled,
            mainPriority = if (state == AdmissionState.Unknown) null else 1,
            highestPassingPriority = if (state == AdmissionState.Enrolled) 1 else null,
            sourceKind = sourceKind,
            sourceUrl = if (sourceKind == null) null else "https://abitur71.tsu.tula.ru/daily",
            checkedAt = LocalDateTime.now().toString(),
            isStale = false,
            warning = if (state == AdmissionState.Enrolled) {
                null
            } else {
                "Демо: нажмите «Проверить статус», чтобы перейти к следующему этапу."
            },
        )
    }

    private fun String.maskApplicantCode(): String = when {
        length <= 4 -> "****"
        else -> take(2) + "*".repeat(length - 4) + takeLast(2)
    }

    private companion object {
        const val CONTRACT_DOCUMENT_ID = "target-contract"
        const val ENROLLMENT_ORDER_DOCUMENT_ID = "enrollment-order"
        val DEMO_PDF = "%PDF-1.4\n% Tseletrack demo contract\n%%EOF\n".encodeToByteArray()

        val offers = listOf(
            TargetOffer(
                id = "338931",
                directionCode = "08.03.01",
                directionName = "Строительство",
                profile = "Промышленное и гражданское строительство",
                programName = "Бакалавриат",
                programLevelName = "Высшее образование",
                educationFormName = "Очная",
                universityName = "ТулГУ",
                proposerName = "АО «Сплав»",
                workplaceAddress = "Тула",
                applicationDeadline = "2026-07-25T23:59:00",
                requestCount = 5,
                requestLinkType = "EPGU_SERVICE",
                officialApplicationUrl = "https://www.gosuslugi.ru/10171/1/form",
                sourceUrl = "https://trudvsem.ru/target-education/view?offeringId=338931",
            ),
            TargetOffer(
                id = "demo-kbp",
                directionCode = "09.03.01",
                directionName = "Информатика и вычислительная техника",
                profile = "Вычислительные машины, комплексы, системы и сети",
                programName = "Бакалавриат",
                programLevelName = "Высшее образование",
                educationFormName = "Очная",
                universityName = "ТулГУ",
                proposerName = "АО «КБП»",
                workplaceAddress = "Тула",
                applicationDeadline = "2026-07-25T23:59:00",
                requestCount = 12,
                requestLinkType = "EPGU_SERVICE",
                officialApplicationUrl = "https://www.gosuslugi.ru/10171/1/form",
                sourceUrl = "https://trudvsem.ru/target-education/search",
            ),
            TargetOffer(
                id = "demo-azot",
                directionCode = "18.03.01",
                directionName = "Химическая технология",
                profile = "Химическая технология неорганических веществ",
                programName = "Бакалавриат",
                programLevelName = "Высшее образование",
                educationFormName = "Очная",
                universityName = "ТулГУ",
                proposerName = "АО «НАК „Азот“»",
                workplaceAddress = "Новомосковск",
                applicationDeadline = "2026-07-25T23:59:00",
                requestCount = 3,
                requestLinkType = "EPGU_SERVICE",
                officialApplicationUrl = "https://www.gosuslugi.ru/10171/1/form",
                sourceUrl = "https://trudvsem.ru/target-education/search",
            ),
        )
    }
}
