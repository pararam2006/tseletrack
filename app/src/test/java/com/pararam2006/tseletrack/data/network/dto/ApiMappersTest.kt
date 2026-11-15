package com.pararam2006.tseletrack.data.network.dto

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.AdmissionSourceKind
import com.pararam2006.tseletrack.domain.student.AdmissionState
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
                {"id": 1, "stage_type": "career_guidance", "title": "Первый", "status": "completed"},
                {"id": 2, "stage_type": "university_choice", "title": "Второй", "status": "current"},
                {"id": 3, "stage_type": "contract", "title": "Третий", "status": "waiting"},
                {"id": 4, "stage_type": "education", "title": "Четвёртый", "status": "future"}
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

    @Test
    fun `target offer response keeps external string id and stale warning`() {
        val dto = json.decodeFromString<TargetOfferListDto>(
            """
            {
              "items": [{
                "id": "338931",
                "direction_code": "08.03.01",
                "direction_name": "Строительство",
                "profile": "Промышленное и гражданское строительство",
                "program_name": "Бакалавриат",
                "program_level_name": "Высшее образование",
                "education_form_name": "Очная",
                "university_code": "1027100753360",
                "university_name": "ТулГУ",
                "proposer_name": "АО Заказчик",
                "workplace_address": "Тула",
                "workplace_okved_code": "62.01",
                "application_deadline": "2026-12-01",
                "request_count": 2,
                "request_link_type": "EPGU_SERVICE",
                "official_application_url": "https://www.gosuslugi.ru/60074/1/form",
                "source_status": "PUBLISHED",
                "source_url": "https://trudvsem.ru/target-education/view?offeringId=338931",
                "fetched_at": "2026-11-15T12:00:00Z"
              }],
              "fetched_at": "2026-11-15T12:00:00Z",
              "is_stale": true,
              "warning": "Источник временно недоступен"
            }
            """.trimIndent(),
        )

        val catalog = dto.toDomain()

        assertEquals("338931", catalog.items.single().id)
        assertEquals("08.03.01", catalog.items.single().directionCode)
        assertEquals("EPGU_SERVICE", catalog.items.single().requestLinkType)
        assertEquals(
            "https://www.gosuslugi.ru/60074/1/form",
            catalog.items.single().officialApplicationUrl,
        )
        assertEquals(true, catalog.isStale)
        assertEquals("Источник временно недоступен", catalog.warning)
    }

    @Test
    fun `old target offer response falls back to source url for application`() {
        val dto = json.decodeFromString<TargetOfferListDto>(
            """
            {
              "items": [{
                "id": "338931",
                "university_code": "1027100753360",
                "source_url": "https://trudvsem.ru/target-education/view?offeringId=338931",
                "fetched_at": "2026-11-15T12:00:00Z"
              }],
              "is_stale": false
            }
            """.trimIndent(),
        )

        val offer = dto.toDomain().items.single()

        assertEquals(null, offer.requestLinkType)
        assertEquals(offer.sourceUrl, offer.officialApplicationUrl)
    }

    @Test
    fun `blank official application url falls back to source url`() {
        val dto = json.decodeFromString<TargetOfferListDto>(
            """
            {
              "items": [{
                "id": "338931",
                "university_code": "1027100753360",
                "official_application_url": "   ",
                "source_url": "https://trudvsem.ru/target-education/view?offeringId=338931",
                "fetched_at": "2026-11-15T12:00:00Z"
              }],
              "is_stale": false
            }
            """.trimIndent(),
        )

        val offer = dto.toDomain().items.single()

        assertEquals(offer.sourceUrl, offer.officialApplicationUrl)
    }

    @Test
    fun `official admission response maps normalized enums`() {
        val dto = json.decodeFromString<AdmissionStatusDto>(
            """
            {
              "applicant_code_masked": "17***14",
              "state": "enrolled",
              "direction_code": "01.03.02",
              "direction_name": "Прикладная математика и информатика",
              "profile": "Искусственный интеллект",
              "quota": "target",
              "sponsor_name": "АО Заказчик",
              "total_score": 291,
              "consent_to_enroll": true,
              "main_priority": 1,
              "highest_passing_priority": 6,
              "source_kind": "enrollment_order_list",
              "source_url": "https://abitur.tsu.tula.ru/2026/zach/list.pdf",
              "source_formed_at": "2026-08-03T23:39:35+03:00",
              "checked_at": "2026-11-15T12:00:00Z",
              "is_stale": false,
              "warning": null
            }
            """.trimIndent(),
        )

        val status = dto.toDomain()

        assertEquals(AdmissionState.Enrolled, status.state)
        assertEquals(AdmissionSourceKind.EnrollmentOrderList, status.sourceKind)
        assertEquals(291, status.totalScore)
        assertEquals("17***14", status.applicantCodeMasked)
    }

    private fun user(role: String) = UserDto(
        id = 1,
        email = "user@example.com",
        fullName = "User",
        role = role,
        isActive = true,
    )
}
