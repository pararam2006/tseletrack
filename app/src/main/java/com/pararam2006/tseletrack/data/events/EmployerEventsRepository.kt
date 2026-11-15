package com.pararam2006.tseletrack.data.events

import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.ManagedEmployerEvent

interface EmployerEventsRepository {
    suspend fun loadForStudent(organizationName: String): List<EmployerEventSummary>

    suspend fun setRegistration(
        eventId: String,
        isRegistered: Boolean,
    ): EmployerEventSummary

    suspend fun loadForEmployer(organizationName: String): List<ManagedEmployerEvent>
}

/** An expected domain error that can be safely shown to the user. */
class EmployerEventsException(message: String) : IllegalStateException(message)
