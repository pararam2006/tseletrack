package com.pararam2006.tseletrack.data.network.dto

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ApiMappersTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `server roles map to navigation roles`() {
        assertEquals(Role.Student, user(role = "student").toRole())
        assertEquals(Role.University, user(role = "university").toRole())
        assertEquals(Role.Company, user(role = "company").toRole())
        assertEquals(Role.Admin, user(role = "admin").toRole())
    }

    @Test
    fun `unknown role is rejected`() {
        assertThrows(ApiException::class.java) { user(role = "owner").toRole() }
    }

    @Test
    fun `student snake case response maps and clamps percentages`() {
        val dto = json.decodeFromString<StudentDashboardDto>(
            """
            {
              "route_id": 3,
              "route_progress_percent": 140,
              "route_stages": [
                {"id": 1, "title": "Первый", "status": "completed"},
                {"id": 2, "title": "Второй", "status": "current"},
                {"id": 3, "title": "Третий", "status": "waiting"},
                {"id": 4, "title": "Четвёртый", "status": "future"}
              ],
              "universities": [
                {"id": 4, "name": "ТулГУ", "fields": "ИТ", "match_percent": 92}
              ],
              "tasks": [{"id": 5, "title": "Задача", "is_completed": true}],
              "profile": {
                "display_name": "Иван",
                "avatar_letter": "И",
                "role": "Студент",
                "document_count": 2
              }
            }
            """.trimIndent(),
        )

        val dashboard = dto.toDomain()

        assertEquals(100, dashboard.routeProgressPercent)
        assertEquals(
            listOf(
                RouteStageStatus.Completed,
                RouteStageStatus.Current,
                RouteStageStatus.Waiting,
                RouteStageStatus.Future,
            ),
            dashboard.routeStages.map { it.status },
        )
        assertEquals("4", dashboard.universities.single().id)
        assertEquals(true, dashboard.tasks.single().isCompleted)
    }

    @Test
    fun `candidate statuses have stable api values`() {
        assertEquals("education", CandidateStatus.Education.toApiValue())
        assertEquals("employment", CandidateStatus.Employment.toApiValue())
        assertEquals("retention", CandidateStatus.Retention.toApiValue())
        assertEquals("completed", CandidateStatus.Completed.toApiValue())
    }

    private fun user(role: String) = UserDto(
        id = 1,
        email = "user@example.com",
        fullName = "User",
        role = role,
        isActive = true,
    )
}
