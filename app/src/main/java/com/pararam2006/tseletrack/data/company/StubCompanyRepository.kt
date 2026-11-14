package com.pararam2006.tseletrack.data.company

import com.pararam2006.tseletrack.data.university.defaultAnalytics
import com.pararam2006.tseletrack.domain.role.CandidateItem
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard
import com.pararam2006.tseletrack.domain.role.RoleProfile

/** In-memory employer data source. Replace with API calls when endpoints are available. */
class StubCompanyRepository : CompanyRepository {
    override suspend fun loadDashboard(): CompanyDashboard = CompanyDashboard(
        candidates = listOf(
            CandidateItem("b", "Студент Б.", "Б", "ТулГУ", "Обучение", 50, CandidateStatus.Education),
            CandidateItem("v", "Студент В.", "В", "ТГПУ", "Трудоустройство", 83, CandidateStatus.Employment),
            CandidateItem("d", "Студент Д.", "Д", "ТулГУ", "Удержание", 100, CandidateStatus.Completed),
        ),
        analytics = defaultAnalytics(),
        profile = RoleProfile("ООО «Завод»", "З", "Работодатель"),
    )

    override suspend fun updateCandidateStatus(candidateId: String, status: CandidateStatus) = Unit
    override suspend fun declineCandidate(candidateId: String) = Unit
    override suspend fun logout() = Unit
}
