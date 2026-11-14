package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.RouteStage
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.domain.student.StudentTask
import com.pararam2006.tseletrack.domain.student.University

/** Temporary in-memory data source. Replace its methods with API calls when the backend is ready. */
class StubStudentRepository : StudentRepository {
    override suspend fun loadDashboard(): StudentDashboard = StudentDashboard(
        routeProgressPercent = 33,
        routeStages = listOf(
            RouteStage("career-guidance", "Профориентация", RouteStageStatus.Completed),
            RouteStage("university-choice", "Выбор университета", RouteStageStatus.Completed),
            RouteStage("contract", "Заключение договора", RouteStageStatus.Current),
            RouteStage("education", "Обучение", RouteStageStatus.Waiting),
            RouteStage("employment", "Трудоустройство", RouteStageStatus.Future),
            RouteStage("retention", "Удержание", RouteStageStatus.Future),
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

    override suspend fun chooseUniversity(universityId: String) = Unit

    override suspend fun updateTask(taskId: String, isCompleted: Boolean) = Unit

    override suspend fun addTask() = Unit

    override suspend fun logout() = Unit
}
