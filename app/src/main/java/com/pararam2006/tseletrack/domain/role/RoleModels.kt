package com.pararam2006.tseletrack.domain.role

data class RoleProfile(
    val displayName: String,
    val avatarLetter: String,
    val role: String,
)

data class ContractQueueItem(
    val id: String,
    val studentName: String,
    val avatarLetter: String,
    val signedAt: String,
)

data class UniversityStudentItem(
    val id: String,
    val name: String,
    val stage: String,
    val progressPercent: Int,
)

enum class CandidateStatus(val title: String) {
    Education("Обучение"),
    Employment("Работа"),
    Retention("Удержание"),
    Completed("Завершён"),
}

data class CandidateItem(
    val id: String,
    val name: String,
    val avatarLetter: String,
    val university: String,
    val stage: String,
    val progressPercent: Int,
    val status: CandidateStatus,
)

data class AnalyticsDashboard(
    val candidates: Int,
    val employedPercent: Int,
    val inEducation: Int,
    val waitingDecision: Int,
    val universityValues: List<Pair<String, Int>>,
)

data class UniversityDashboard(
    val studentCount: Int,
    val queueCount: Int,
    val availablePlaces: Int,
    val queue: List<ContractQueueItem>,
    val students: List<UniversityStudentItem>,
    val analytics: AnalyticsDashboard,
    val profile: RoleProfile,
)

data class CompanyDashboard(
    val candidates: List<CandidateItem>,
    val analytics: AnalyticsDashboard,
    val profile: RoleProfile,
)

data class StageMetric(
    val title: String,
    val percent: Int,
)

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val avatarLetter: String,
    val role: String,
)

data class SystemEvent(
    val title: String,
    val date: String,
)

data class AdminDashboard(
    val userCount: Int,
    val studentCount: Int,
    val contractsWaiting: Int,
    val employedPercent: Int,
    val stages: List<StageMetric>,
    val users: List<AdminUser>,
    val events: List<SystemEvent>,
    val profile: RoleProfile,
)
