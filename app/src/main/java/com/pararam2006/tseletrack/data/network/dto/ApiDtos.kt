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
    @SerialName("stage_type") val stageType: String,
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
data class StageUpdateRequestDto(
    val status: String,
)

@Serializable
data class ContractDto(
    val id: Int,
    @SerialName("route_id") val routeId: Int,
    val status: String,
    @SerialName("signed_at") val signedAt: String? = null,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class TargetOfferDto(
    val id: String,
    @SerialName("direction_code") val directionCode: String? = null,
    @SerialName("direction_name") val directionName: String? = null,
    val profile: String? = null,
    @SerialName("program_name") val programName: String? = null,
    @SerialName("program_level_name") val programLevelName: String? = null,
    @SerialName("education_form_name") val educationFormName: String? = null,
    @SerialName("university_code") val universityCode: String,
    @SerialName("university_name") val universityName: String? = null,
    @SerialName("proposer_name") val proposerName: String? = null,
    @SerialName("workplace_address") val workplaceAddress: String? = null,
    @SerialName("workplace_okved_code") val workplaceOkvedCode: String? = null,
    @SerialName("application_deadline") val applicationDeadline: String? = null,
    @SerialName("request_count") val requestCount: Int? = null,
    @SerialName("request_link_type") val requestLinkType: String? = null,
    @SerialName("official_application_url") val officialApplicationUrl: String? = null,
    @SerialName("source_status") val sourceStatus: String? = null,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("fetched_at") val fetchedAt: String,
)

@Serializable
data class TargetOfferListDto(
    val items: List<TargetOfferDto>,
    @SerialName("fetched_at") val fetchedAt: String? = null,
    @SerialName("is_stale") val isStale: Boolean,
    val warning: String? = null,
)

@Serializable
data class TargetOfferSelectionDto(
    val offer: TargetOfferDto,
    val status: String,
    @SerialName("selected_at") val selectedAt: String,
)

@Serializable
data class AdmissionTrackingRequestDto(
    @SerialName("applicant_code") val applicantCode: String,
)

@Serializable
data class AdmissionStatusDto(
    @SerialName("applicant_code_masked") val applicantCodeMasked: String,
    val state: String,
    @SerialName("direction_code") val directionCode: String? = null,
    @SerialName("direction_name") val directionName: String? = null,
    val profile: String? = null,
    val quota: String,
    @SerialName("sponsor_name") val sponsorName: String? = null,
    @SerialName("total_score") val totalScore: Int? = null,
    @SerialName("consent_to_enroll") val consentToEnroll: Boolean? = null,
    @SerialName("main_priority") val mainPriority: Int? = null,
    @SerialName("highest_passing_priority") val highestPassingPriority: Int? = null,
    @SerialName("source_kind") val sourceKind: String? = null,
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("source_formed_at") val sourceFormedAt: String? = null,
    @SerialName("checked_at") val checkedAt: String? = null,
    @SerialName("is_stale") val isStale: Boolean,
    val warning: String? = null,
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
