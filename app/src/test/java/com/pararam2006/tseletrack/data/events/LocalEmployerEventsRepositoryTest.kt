package com.pararam2006.tseletrack.data.events

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.session.SessionIdentity
import com.pararam2006.tseletrack.data.session.SessionIdentityStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class LocalEmployerEventsRepositoryTest {

    @Test
    fun `student can register and cancel registration`() = runTest {
        val fixture = fixture(student("student-1", "Иван Петров"))
        val before = fixture.repository.loadForStudent("АО Сплав").first()

        val registered = fixture.repository.setRegistration(before.event.id, isRegistered = true)

        assertTrue(registered.isRegistered)
        assertEquals(before.registeredCount + 1, registered.registeredCount)

        val cancelled = fixture.repository.setRegistration(before.event.id, isRegistered = false)

        assertFalse(cancelled.isRegistered)
        assertEquals(before.registeredCount, cancelled.registeredCount)
    }

    @Test
    fun `registration belongs to current account while total is shared`() = runTest {
        val fixture = fixture(student("student-1", "Иван Петров"))
        val event = fixture.repository.loadForStudent("Сплав").first()
        fixture.repository.setRegistration(event.event.id, isRegistered = true)

        fixture.identityStore.set(student("student-2", "Мария Соколова"))
        val secondStudentView = fixture.repository.loadForStudent("Сплав").first()

        assertFalse(secondStudentView.isRegistered)
        assertEquals(event.registeredCount + 1, secondStudentView.registeredCount)
    }

    @Test
    fun `employer sees student registration in participant preview`() = runTest {
        val fixture = fixture(student("student-1", "Иван Петров"))
        val event = fixture.repository.loadForStudent("Сплав").first()
        fixture.repository.setRegistration(event.event.id, isRegistered = true)

        fixture.identityStore.set(
            SessionIdentity("company-1", "АО Сплав", Role.Company),
        )
        val employerEvent = fixture.repository.loadForEmployer("АО «Сплав»")
            .first { it.event.id == event.event.id }

        assertEquals(event.registeredCount + 1, employerEvent.registeredCount)
        assertTrue(
            employerEvent.participants.any {
                it.id == "student-1" && it.displayName == "Иван Петров"
            },
        )
    }

    @Test
    fun `catalog is filtered by normalized organization name`() = runTest {
        val fixture = fixture(student("student-1", "Иван Петров"))

        val kbpEvents = fixture.repository.loadForStudent("Акционерное общество КБП")
        val rostecEvents = fixture.repository.loadForEmployer("Ростех")
        val unknownEvents = fixture.repository.loadForStudent("Неизвестный работодатель")

        assertEquals(2, kbpEvents.size)
        assertTrue(kbpEvents.all { it.event.organizationName.contains("КБП") })
        assertEquals(2, rostecEvents.size)
        assertTrue(unknownEvents.isEmpty())
    }

    @Test
    fun `company cannot register as student`() = runTest {
        val fixture = fixture(
            SessionIdentity("company-1", "АО Сплав", Role.Company),
        )
        val eventId = fixture.repository.loadForEmployer("Сплав").first().event.id

        try {
            fixture.repository.setRegistration(eventId, isRegistered = true)
            fail("Expected EmployerEventsException")
        } catch (error: EmployerEventsException) {
            assertEquals("Запись доступна только абитуриентам", error.message)
        }
    }

    @Test
    fun `duplicate registration is idempotent`() = runTest {
        val fixture = fixture(student("student-1", "Иван Петров"))
        val event = fixture.repository.loadForStudent("Сплав").first()

        val first = fixture.repository.setRegistration(event.event.id, isRegistered = true)
        val second = fixture.repository.setRegistration(event.event.id, isRegistered = true)

        assertEquals(first.registeredCount, second.registeredCount)
        assertTrue(second.isRegistered)
    }

    private fun fixture(identity: SessionIdentity): Fixture {
        val identityStore = SessionIdentityStore().apply { set(identity) }
        return Fixture(
            identityStore = identityStore,
            repository = LocalEmployerEventsRepository(DemoPlatformStore(), identityStore),
        )
    }

    private fun student(id: String, name: String) = SessionIdentity(id, name, Role.Student)

    private data class Fixture(
        val identityStore: SessionIdentityStore,
        val repository: LocalEmployerEventsRepository,
    )
}
