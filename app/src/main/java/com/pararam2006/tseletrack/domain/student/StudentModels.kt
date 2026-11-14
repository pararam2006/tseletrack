package com.pararam2006.tseletrack.domain.student

enum class RouteStageStatus {
    Completed,
    Current,
    Waiting,
    Future,
}

data class RouteStage(
    val id: String,
    val title: String,
    val status: RouteStageStatus,
)

enum class StudyField(val title: String) {
    ComputerScience("Информатика"),
    Engineering("Инженерия"),
}

data class University(
    val id: String,
    val name: String,
    val fields: String,
    val matchPercent: Int,
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
