package com.pararam2006.tseletrack.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    @SerialName("full_name") val fullName: String,
    val role: String = "student",
)

@Serializable
data class TokenResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    @SerialName("full_name") val fullName: String,
    val role: String,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class ProfileDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_letter") val avatarLetter: String,
    val role: String,
)

@Serializable
data class StudentProfileDto(
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_letter") val avatarLetter: String,
    val role: String,
    @SerialName("document_count") val documentCount: Int,
)

@Serializable
data class RouteStageDto(
    val id: Int,
    val title: String,
    val status: String,
)

@Serializable
data class UniversityDto(
    val id: Int,
    val name: String,
    val fields: String,
    @SerialName("match_percent") val matchPercent: Int,
)

@Serializable
data class StudentTaskDto(
    val id: Int,
    val title: String,
    @SerialName("is_completed") val isCompleted: Boolean,
)

@Serializable
data class StudentDashboardDto(
    @SerialName("route_id") val routeId: Int,
    @SerialName("route_progress_percent") val routeProgressPercent: Int,
    @SerialName("route_stages") val routeStages: List<RouteStageDto>,
    val universities: List<UniversityDto>,
    val tasks: List<StudentTaskDto>,
    val profile: StudentProfileDto,
)

@Serializable
data class TaskUpdateRequestDto(
    @SerialName("is_completed") val isCompleted: Boolean,
)

@Serializable
data class TaskCreateRequestDto(val title: String)

@Serializable
data class ContractDto(
    val id: Int,
    @SerialName("route_id") val routeId: Int,
    val status: String,
    @SerialName("signed_at") val signedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class UniversitySelectionDto(
    @SerialName("route_id") val routeId: Int,
    @SerialName("university_id") val universityId: Int,
    val status: String,
    @SerialName("selected_at") val selectedAt: String,
)

@Serializable
data class ContractQueueItemDto(
    val id: Int,
    @SerialName("student_name") val studentName: String,
    @SerialName("avatar_letter") val avatarLetter: String,
    @SerialName("signed_at") val signedAt: String,
)

@Serializable
data class UniversityStudentItemDto(
    val id: Int,
    val name: String,
    val stage: String,
    @SerialName("progress_percent") val progressPercent: Int,
)

@Serializable
data class UniversityValueDto(
    val name: String,
    val value: Int,
)

@Serializable
data class AnalyticsDashboardDto(
    val candidates: Int,
    @SerialName("employed_percent") val employedPercent: Int,
    @SerialName("in_education") val inEducation: Int,
    @SerialName("waiting_decision") val waitingDecision: Int,
    @SerialName("university_values") val universityValues: List<UniversityValueDto>,
)

@Serializable
data class UniversityDashboardDto(
    @SerialName("student_count") val studentCount: Int,
    @SerialName("queue_count") val queueCount: Int,
    @SerialName("available_places") val availablePlaces: Int,
    val queue: List<ContractQueueItemDto>,
    val students: List<UniversityStudentItemDto>,
    val analytics: AnalyticsDashboardDto,
    val profile: ProfileDto,
)

@Serializable
data class CandidateDto(
    val id: Int,
    val name: String,
    @SerialName("avatar_letter") val avatarLetter: String,
    val university: String,
    val stage: String,
    @SerialName("progress_percent") val progressPercent: Int,
    val status: String,
)

@Serializable
data class CandidateStatusRequestDto(val status: String)

@Serializable
data class CompanyDashboardDto(
    val candidates: List<CandidateDto>,
    val analytics: AnalyticsDashboardDto,
    val profile: ProfileDto,
)

@Serializable
data class StageMetricDto(
    val title: String,
    val percent: Int,
)

@Serializable
data class AdminUserDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerialName("avatar_letter") val avatarLetter: String,
    val role: String,
)

@Serializable
data class SystemEventDto(
    val title: String,
    val date: String,
)

@Serializable
data class AdminDashboardDto(
    @SerialName("user_count") val userCount: Int,
    @SerialName("student_count") val studentCount: Int,
    @SerialName("contracts_waiting") val contractsWaiting: Int,
    @SerialName("employed_percent") val employedPercent: Int,
    val stages: List<StageMetricDto>,
    val users: List<AdminUserDto>,
    val events: List<SystemEventDto>,
    val profile: ProfileDto,
)
