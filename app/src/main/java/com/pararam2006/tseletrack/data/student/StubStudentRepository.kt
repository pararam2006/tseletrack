package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.StudentTask
import com.pararam2006.tseletrack.domain.student.University
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection

/** Temporary in-memory data source. Replace its methods with API calls when the backend is ready. */
class StubStudentRepository : StudentRepository {
    override suspend fun loadDashboard(): StudentDashboard = StudentDashboard(
        routeProgressPercent = 33,
        routeStages = listOf(
            RouteStage("career-guidance", RouteStageType.CareerGuidance, "Профориентация", RouteStageStatus.Completed),
            RouteStage("target-offer", RouteStageType.TargetOfferChoice, "Выбор целевого предложения", RouteStageStatus.Completed),
            RouteStage("contract", RouteStageType.Contract, "Заключение договора", RouteStageStatus.Current),
            RouteStage("education", RouteStageType.Education, "Обучение", RouteStageStatus.Waiting),
            RouteStage("employment", RouteStageType.Employment, "Трудоустройство", RouteStageStatus.Future),
            RouteStage("retention", RouteStageType.Retention, "Удержание", RouteStageStatus.Future),
        ),
        universities = listOf(
            University("tulsu", "ТулГУ", "Информатика, инженерия", 92),
            University("university-2", "Университет 2", "Информатика", 81),
            University("university-3", "Университет 3", "Инженерия", 74),
        ),
        tasks = listOf(
            StudentTask("passport", "Загрузить паспортные данные", isCompleted = false),
            StudentTask("contract", "Подписать договор до 30 ноября", isCompleted = false),
            StudentTask("university", "Выбрать вуз", isCompleted = true),
        ),
        profile = StudentProfile(
            displayName = "Студент А.",
            avatarLetter = "А",
            role = "Студент",
            documentCount = 1,
        ),
    )

    override suspend fun signContract() = Unit

    private val offer = TargetOffer(
        id = "338931",
        directionCode = "08.03.01",
        directionName = "Строительство",
        profile = "Промышленное и гражданское строительство",
        programName = "Бакалавриат",
        programLevelName = "Высшее образование",
        educationFormName = "Очная",
        universityName = "ТулГУ",
        proposerName = "ООО «Тульские горелки»",
        workplaceAddress = "Тульская область",
        applicationDeadline = null,
        requestCount = 0,
        requestLinkType = "EPGU_SERVICE",
        officialApplicationUrl = "https://www.gosuslugi.ru/60074/1/form",
        sourceUrl = "https://trudvsem.ru/target-education/view?offeringId=338931",
    )

    override suspend fun loadTargetOffers(forceRefresh: Boolean) =
        TargetOfferCatalog(listOf(offer), isStale = false, warning = null)

    override suspend fun loadTargetOfferSelection(): TargetOfferSelection? = null

    override suspend fun chooseTargetOffer(offerId: String) =
        TargetOfferSelection(offer, "selected_locally")

    override suspend fun loadAdmissionStatus(): AdmissionStatus? = null

    override suspend fun saveApplicantCode(applicantCode: String) = AdmissionStatus(
        applicantCodeMasked = "12**56",
        state = AdmissionState.Unknown,
        directionCode = null,
        directionName = null,
        profile = null,
        quota = "unknown",
        sponsorName = null,
        totalScore = null,
        consentToEnroll = null,
        mainPriority = null,
        highestPassingPriority = null,
        sourceKind = null,
        sourceUrl = null,
        checkedAt = null,
        isStale = false,
        warning = null,
    )

    override suspend fun refreshAdmissionStatus() =
        saveApplicantCode("123456")

    override suspend fun updateTask(taskId: String, isCompleted: Boolean) = Unit

    override suspend fun addTask() = Unit

    override suspend fun completeCareerGuidance() = Unit

    override suspend fun logout() = Unit
}
