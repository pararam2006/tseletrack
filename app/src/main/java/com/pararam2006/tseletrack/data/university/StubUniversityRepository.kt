package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.domain.role.ContractQueueItem
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.role.UniversityStudentItem

/** In-memory university data source. Replace with API calls when endpoints are available. */
class StubUniversityRepository : UniversityRepository {
    override suspend fun loadDashboard(): UniversityDashboard = UniversityDashboard(
        studentCount = 24,
        queueCount = 3,
        availablePlaces = 120,
        queue = listOf(
            ContractQueueItem("a", "Студент А.", "А", "Договор подписан 14.11"),
            ContractQueueItem("zh", "Студент Ж.", "Ж", "Договор подписан 13.11"),
            ContractQueueItem("b", "Студент Б.", "Б", "Договор подписан 12.11"),
        ),
        students = listOf(
            UniversityStudentItem("a", "Студент А.", "Договор", 33),
            UniversityStudentItem("b", "Студент Б.", "Обучение", 48),
            UniversityStudentItem("v", "Студент В.", "Трудоустройство", 83),
            UniversityStudentItem("g", "Студент Г.", "Выбор вуза", 17),
            UniversityStudentItem("d", "Студент Д.", "Трудоустроен", 100),
            UniversityStudentItem("e", "Студент Е.", "Обучение", 48),
        ),
        analytics = defaultAnalytics(),
        profile = RoleProfile("ТулГУ", "Т", "Вуз"),
    )

    override suspend fun confirmContract(contractId: String) = Unit
    override suspend fun declineContract(contractId: String) = Unit
    override suspend fun logout() = Unit
}

internal fun defaultAnalytics() = AnalyticsDashboard(
    candidates = 12,
    employedPercent = 64,
    inEducation = 8,
    waitingDecision = 3,
    universityValues = listOf("ТулГУ" to 12, "ТГПУ" to 8, "Вуз 3" to 5, "Вуз 4" to 3),
)
