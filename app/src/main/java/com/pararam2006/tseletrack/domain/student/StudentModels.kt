package com.pararam2006.tseletrack.domain.student

enum class RouteStageStatus {
    Completed,
    Current,
    Waiting,
    Future,
}

enum class RouteStageType {
    CareerGuidance,
    TargetOfferChoice,
    Contract,
    Education,
    Employment,
    Other,
}

data class RouteStage(
    val id: String,
    val type: RouteStageType,
    val title: String,
    val status: RouteStageStatus,
)

data class University(
    val id: String,
    val name: String,
    val fields: String,
    val matchPercent: Int,
)

data class TargetOffer(
    val id: String,
    val directionCode: String?,
    val directionName: String?,
    val profile: String?,
    val programName: String?,
    val programLevelName: String?,
    val educationFormName: String?,
    val universityName: String?,
    val proposerName: String?,
    val workplaceAddress: String?,
    val applicationDeadline: String?,
    val requestCount: Int?,
    val requestLinkType: String?,
    val officialApplicationUrl: String,
    val sourceUrl: String,
)

data class TargetOfferCatalog(
    val items: List<TargetOffer>,
    val isStale: Boolean,
    val warning: String?,
)

data class TargetOfferSelection(
    val offer: TargetOffer,
    val status: String,
)

enum class AdmissionState {
    Unknown,
    ApplicationFound,
    Recommended,
    Enrolled,
}

enum class AdmissionSourceKind {
    DailyList,
    EnrollmentOrderList,
}

data class AdmissionStatus(
    val applicantCodeMasked: String,
    val state: AdmissionState,
    val directionCode: String?,
    val directionName: String?,
    val profile: String?,
    val quota: String,
    val sponsorName: String?,
    val totalScore: Int?,
    val consentToEnroll: Boolean?,
    val mainPriority: Int?,
    val highestPassingPriority: Int?,
    val sourceKind: AdmissionSourceKind?,
    val sourceUrl: String?,
    val checkedAt: String?,
    val isStale: Boolean,
    val warning: String?,
)

data class StudentTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
)

data class StudentProfile(
    val displayName: String,
    val avatarLetter: String,
    val role: String,
    val documentCount: Int,
)

data class StudentDashboard(
    val routeProgressPercent: Int,
    val routeStages: List<RouteStage>,
    val universities: List<University>,
    val tasks: List<StudentTask>,
    val profile: StudentProfile,
)
