package com.pararam2006.tseletrack.data.network.dto

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.domain.role.AdminDashboard
import com.pararam2006.tseletrack.domain.role.AdminUser
import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.domain.role.CandidateItem
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard
import com.pararam2006.tseletrack.domain.role.ContractQueueItem
import com.pararam2006.tseletrack.domain.role.RoleProfile
import com.pararam2006.tseletrack.domain.role.StageMetric
import com.pararam2006.tseletrack.domain.role.SystemEvent
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.role.UniversityStudentItem
import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.StudentTask
import com.pararam2006.tseletrack.domain.student.University
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal fun UserDto.toRole(): Role = when (role.lowercase()) {
    "student" -> Role.Student
    "university" -> Role.University
    "company" -> Role.Company
    "admin" -> Role.Admin
    else -> throw ApiException(ApiErrorKind.InvalidResponse, "Сервер вернул неизвестную роль: $role")
}

internal fun StudentDashboardDto.toDomain() = StudentDashboard(
    routeProgressPercent = routeProgressPercent.coerceIn(0, 100),
    routeStages = routeStages.map(RouteStageDto::toDomain),
    universities = universities.map(UniversityDto::toDomain),
    tasks = tasks.map(StudentTaskDto::toDomain),
    profile = profile.toDomain(),
)

private fun RouteStageDto.toDomain() = RouteStage(
    id = id.toString(),
    title = title,
    status = when (status.lowercase()) {
        "completed" -> RouteStageStatus.Completed
        "current", "in_progress" -> RouteStageStatus.Current
        "waiting" -> RouteStageStatus.Waiting
        else -> RouteStageStatus.Future
    },
)

private fun UniversityDto.toDomain() = University(
    id = id.toString(),
    name = name,
    fields = fields,
    matchPercent = matchPercent.coerceIn(0, 100),
)

private fun StudentTaskDto.toDomain() = StudentTask(
    id = id.toString(),
    title = title,
    isCompleted = isCompleted,
)

private fun StudentProfileDto.toDomain() = StudentProfile(
    displayName = displayName,
    avatarLetter = avatarLetter.ifBlank { displayName.firstOrNull()?.uppercase() ?: "?" },
    role = role,
    documentCount = documentCount.coerceAtLeast(0),
)

internal fun UniversityDashboardDto.toDomain() = UniversityDashboard(
    studentCount = studentCount,
    queueCount = queueCount,
    availablePlaces = availablePlaces,
    queue = queue.map {
        ContractQueueItem(
            id = it.id.toString(),
            studentName = it.studentName,
            avatarLetter = it.avatarLetter,
            signedAt = "Договор подписан ${it.signedAt.toDisplayDate()}",
        )
    },
    students = students.map {
        UniversityStudentItem(
            id = it.id.toString(),
            name = it.name,
            stage = it.stage,
            progressPercent = it.progressPercent.coerceIn(0, 100),
        )
    },
    analytics = analytics.toDomain(),
    profile = profile.toDomain(),
)

internal fun CompanyDashboardDto.toDomain() = CompanyDashboard(
    candidates = candidates.map(CandidateDto::toDomain),
    analytics = analytics.toDomain(),
    profile = profile.toDomain(),
)

private fun CandidateDto.toDomain() = CandidateItem(
    id = id.toString(),
    name = name,
    avatarLetter = avatarLetter,
    university = university,
    stage = stage,
    progressPercent = progressPercent.coerceIn(0, 100),
    status = status.toCandidateStatus(),
)

internal fun CandidateStatus.toApiValue(): String = when (this) {
    CandidateStatus.Education -> "education"
    CandidateStatus.Employment -> "employment"
    CandidateStatus.Retention -> "retention"
    CandidateStatus.Completed -> "completed"
}

private fun String.toCandidateStatus(): CandidateStatus = when (lowercase()) {
    "education" -> CandidateStatus.Education
    "employment" -> CandidateStatus.Employment
    "retention" -> CandidateStatus.Retention
    "completed" -> CandidateStatus.Completed
    else -> throw ApiException(ApiErrorKind.InvalidResponse, "Неизвестный статус кандидата: $this")
}

private fun AnalyticsDashboardDto.toDomain() = AnalyticsDashboard(
    candidates = candidates,
    employedPercent = employedPercent.coerceIn(0, 100),
    inEducation = inEducation,
    waitingDecision = waitingDecision,
    universityValues = universityValues.map { it.name to it.value },
)

private fun ProfileDto.toDomain() = RoleProfile(
    displayName = displayName,
    avatarLetter = avatarLetter.ifBlank { displayName.firstOrNull()?.uppercase() ?: "?" },
    role = role,
)

internal fun AdminDashboardDto.toDomain() = AdminDashboard(
    userCount = userCount,
    studentCount = studentCount,
    contractsWaiting = contractsWaiting,
    employedPercent = employedPercent.coerceIn(0, 100),
    stages = stages.map { StageMetric(it.title, it.percent.coerceIn(0, 100)) },
    users = users.map {
        AdminUser(
            id = it.id.toString(),
            name = it.name,
            email = it.email,
            avatarLetter = it.avatarLetter,
            role = it.role,
        )
    },
    events = events.map { SystemEvent(it.title, it.date.toDisplayDate()) },
    profile = profile.toDomain(),
)

private fun String.toDisplayDate(): String = runCatching {
    OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("dd.MM"))
}.getOrElse {
    take(10).split('-').let { parts ->
        if (parts.size == 3) "${parts[2]}.${parts[1]}" else this
    }
}
