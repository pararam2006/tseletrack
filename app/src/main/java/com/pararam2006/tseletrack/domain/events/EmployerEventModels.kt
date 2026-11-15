package com.pararam2006.tseletrack.domain.events

import java.time.LocalDateTime

enum class EmployerEventType(val title: String) {
    Excursion("Экскурсия"),
    CareerTalk("Встреча с работодателем"),
    Workshop("Мастер-класс"),
    ProfessionalTrial("Профессиональная проба"),
}

enum class EmployerEventFormat(val title: String) {
    Offline("Очно"),
    Online("Онлайн"),
}

data class EmployerEvent(
    val id: String,
    val organizationName: String,
    val title: String,
    val type: EmployerEventType,
    val format: EmployerEventFormat,
    val startsAt: LocalDateTime,
    val location: String,
    val description: String,
    val capacity: Int,
)

data class EmployerEventSummary(
    val event: EmployerEvent,
    val registeredCount: Int,
    val isRegistered: Boolean,
) {
    val availablePlaces: Int
        get() = (event.capacity - registeredCount).coerceAtLeast(0)

    val canRegister: Boolean
        get() = isRegistered || availablePlaces > 0
}

data class EventParticipant(
    val id: String,
    val displayName: String,
    val registeredAt: LocalDateTime,
)

data class ManagedEmployerEvent(
    val event: EmployerEvent,
    val registeredCount: Int,
    val participants: List<EventParticipant>,
)
