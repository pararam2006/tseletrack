package com.pararam2006.tseletrack.data.events

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.session.SessionIdentityStore
import com.pararam2006.tseletrack.domain.events.EmployerEvent
import com.pararam2006.tseletrack.domain.events.EmployerEventFormat
import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.EmployerEventType
import com.pararam2006.tseletrack.domain.events.EventParticipant
import com.pararam2006.tseletrack.domain.events.ManagedEmployerEvent
import java.time.LocalDateTime
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Deterministic offline event source for the MVP. The interface intentionally mirrors a future
 * backend adapter, while registrations are kept in the shared demo store for cross-role demos.
 */
class LocalEmployerEventsRepository(
    private val demoStore: DemoPlatformStore,
    private val sessionIdentityStore: SessionIdentityStore,
) : EmployerEventsRepository {

    private val registrationsMutex = Mutex()

    override suspend fun loadForStudent(organizationName: String): List<EmployerEventSummary> =
        registrationsMutex.withLock {
            definitions
                .filter { it.event.organizationName.matchesOrganization(organizationName) }
                .sortedBy { it.event.startsAt }
                .map(::toStudentSummary)
        }

    override suspend fun setRegistration(
        eventId: String,
        isRegistered: Boolean,
    ): EmployerEventSummary = registrationsMutex.withLock {
        val identity = sessionIdentityStore.current
            ?: throw EmployerEventsException("Для записи на мероприятие войдите в аккаунт")
        if (identity.role != Role.Student) {
            throw EmployerEventsException("Запись доступна только абитуриентам")
        }
        val definition = definitions.firstOrNull { it.event.id == eventId }
            ?: throw EmployerEventsException("Мероприятие не найдено")
        val registrations = demoStore.eventRegistrations.getOrPut(eventId) { mutableMapOf() }

        if (isRegistered) {
            val currentCount = definition.baseRegisteredCount + registrations.size
            if (identity.id !in registrations && currentCount >= definition.event.capacity) {
                throw EmployerEventsException("Свободных мест больше нет")
            }
            registrations.putIfAbsent(
                identity.id,
                EventParticipant(
                    id = identity.id,
                    displayName = identity.displayName,
                    registeredAt = LocalDateTime.now(),
                ),
            )
        } else {
            registrations.remove(identity.id)
        }

        toStudentSummary(definition)
    }

    override suspend fun loadForEmployer(organizationName: String): List<ManagedEmployerEvent> =
        registrationsMutex.withLock {
            definitions
                .filter { it.event.organizationName.matchesOrganization(organizationName) }
                .sortedBy { it.event.startsAt }
                .map { definition ->
                    val localParticipants = demoStore.eventRegistrations[definition.event.id]
                        .orEmpty()
                        .values
                        .sortedBy(EventParticipant::registeredAt)
                    ManagedEmployerEvent(
                        event = definition.event,
                        registeredCount = definition.baseRegisteredCount + localParticipants.size,
                        participants = definition.previewParticipants + localParticipants,
                    )
                }
        }

    private fun toStudentSummary(definition: EventDefinition): EmployerEventSummary {
        val registrations = demoStore.eventRegistrations[definition.event.id].orEmpty()
        val currentIdentityId = sessionIdentityStore.current?.id
        return EmployerEventSummary(
            event = definition.event,
            registeredCount = definition.baseRegisteredCount + registrations.size,
            isRegistered = currentIdentityId != null && currentIdentityId in registrations,
        )
    }

    private fun String.matchesOrganization(other: String): Boolean {
        val first = normalizeOrganizationName()
        val second = other.normalizeOrganizationName()
        if (first.isEmpty() || second.isEmpty()) return false
        return first == second || first.contains(second) || second.contains(first)
    }

    private fun String.normalizeOrganizationName(): String = lowercase()
        .replace("акционерное общество", " ")
        .split(NON_ALPHANUMERIC)
        .filter { it.isNotBlank() && it !in ORGANIZATION_FORMS }
        .joinToString(separator = "")

    private data class EventDefinition(
        val event: EmployerEvent,
        val baseRegisteredCount: Int,
        val previewParticipants: List<EventParticipant>,
    )

    private companion object {
        val ORGANIZATION_FORMS = setOf("пао", "ао", "гк", "нпо", "ооо")
        val NON_ALPHANUMERIC = Regex("[^а-яёa-z0-9]+")

        fun participant(id: String, name: String, day: Int) = EventParticipant(
            id = id,
            displayName = name,
            registeredAt = LocalDateTime.of(2026, 11, day, 12, 0),
        )

        val definitions = listOf(
            EventDefinition(
                event = EmployerEvent(
                    id = "splav-excursion",
                    organizationName = "АО «Сплав»",
                    title = "Экскурсия в НПО «СПЛАВ»",
                    type = EmployerEventType.Excursion,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 11, 21, 11, 0),
                    location = "Тула, ул. Щегловская Засека, 33",
                    description = "Знакомство с производством и инженерными профессиями предприятия.",
                    capacity = 30,
                ),
                baseRegisteredCount = 18,
                previewParticipants = listOf(
                    participant("splav-1", "Алексей Морозов", 10),
                    participant("splav-2", "Мария Кузнецова", 11),
                    participant("splav-3", "Илья Соколов", 12),
                ),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "splav-talk",
                    organizationName = "АО «Сплав»",
                    title = "Как строится карьера инженера",
                    type = EmployerEventType.CareerTalk,
                    format = EmployerEventFormat.Online,
                    startsAt = LocalDateTime.of(2026, 11, 28, 17, 0),
                    location = "Онлайн-конференция",
                    description = "Инженеры предприятия расскажут о целевом обучении, практике и работе после выпуска.",
                    capacity = 100,
                ),
                baseRegisteredCount = 42,
                previewParticipants = emptyList(),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "kbp-workshop",
                    organizationName = "АО «КБП»",
                    title = "Мастер-класс по цифровому проектированию",
                    type = EmployerEventType.Workshop,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 11, 24, 15, 30),
                    location = "ТулГУ, главный корпус, аудитория 401",
                    description = "Практическое занятие с инженерами КБП и знакомство с реальными производственными задачами.",
                    capacity = 25,
                ),
                baseRegisteredCount = 21,
                previewParticipants = listOf(
                    participant("kbp-1", "Анна Лебедева", 9),
                    participant("kbp-2", "Дмитрий Волков", 10),
                ),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "kbp-trial",
                    organizationName = "АО «КБП»",
                    title = "Профессиональная проба: инженер-программист",
                    type = EmployerEventType.ProfessionalTrial,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 12, 5, 10, 0),
                    location = "Тула, территория АО «КБП»",
                    description = "Командная задача по программированию и встреча с наставниками предприятия.",
                    capacity = 20,
                ),
                baseRegisteredCount = 12,
                previewParticipants = emptyList(),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "azot-excursion",
                    organizationName = "АО «НАК „Азот“»",
                    title = "День химика на производстве",
                    type = EmployerEventType.Excursion,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 11, 29, 9, 30),
                    location = "Новомосковск, ул. Связи, 10",
                    description = "Экскурсия в лабораторию и разговор о направлениях подготовки ТулГУ.",
                    capacity = 35,
                ),
                baseRegisteredCount = 16,
                previewParticipants = emptyList(),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "tulatochmash-excursion",
                    organizationName = "АО «Тулаточмаш»",
                    title = "Открытая инженерная суббота",
                    type = EmployerEventType.Excursion,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 11, 22, 11, 0),
                    location = "Тула, ул. Коминтерна, 24",
                    description = "Экскурсия по предприятию, встреча с молодыми специалистами и ответы по целевому договору.",
                    capacity = 40,
                ),
                baseRegisteredCount = 27,
                previewParticipants = listOf(
                    participant("tulatochmash-1", "Екатерина Орлова", 8),
                    participant("tulatochmash-2", "Никита Павлов", 9),
                ),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "rostec-talk",
                    organizationName = "ГК «Ростех»",
                    title = "Целевое обучение и карьера в Ростехе",
                    type = EmployerEventType.CareerTalk,
                    format = EmployerEventFormat.Online,
                    startsAt = LocalDateTime.of(2026, 11, 26, 18, 0),
                    location = "Онлайн-конференция",
                    description = "Представители предприятий расскажут о проектах, практике и условиях целевого обучения.",
                    capacity = 150,
                ),
                baseRegisteredCount = 84,
                previewParticipants = listOf(
                    participant("rostec-1", "Софья Фролова", 9),
                    participant("rostec-2", "Максим Егоров", 10),
                    participant("rostec-3", "Полина Васильева", 10),
                ),
            ),
            EventDefinition(
                event = EmployerEvent(
                    id = "rostec-trial",
                    organizationName = "ГК «Ростех»",
                    title = "Инженерный кейс-чемпионат",
                    type = EmployerEventType.ProfessionalTrial,
                    format = EmployerEventFormat.Offline,
                    startsAt = LocalDateTime.of(2026, 12, 12, 10, 0),
                    location = "ТулГУ, учебный корпус № 9",
                    description = "Командное решение производственного кейса с экспертами предприятий Ростеха.",
                    capacity = 60,
                ),
                baseRegisteredCount = 38,
                previewParticipants = emptyList(),
            ),
        )
    }
}
