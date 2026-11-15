package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.session.SessionIdentity
import com.pararam2006.tseletrack.data.session.SessionIdentityStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StubAcademicPerformanceRepositoryTest {
    @Test
    fun `demo performance is generated without network after connection`() = runTest {
        val (_, repository) = repositoryForStudent("student-1")

        assertNull(repository.load())
        val performance = repository.connect("12345678", "ИВТ-221")

        assertTrue(performance.isDemo)
        assertTrue(performance.results.isNotEmpty())
        assertEquals(setOf(1, 2), performance.results.map { it.semester }.toSet())
        assertNotNull(repository.load())
    }

    @Test
    fun `academic performance is isolated between student accounts`() = runTest {
        val store = DemoPlatformStore()
        val identityStore = SessionIdentityStore()
        val repository = StubAcademicPerformanceRepository(store, identityStore)

        identityStore.set(studentIdentity("student-1"))
        repository.connect("11111111", "ИВТ-101")

        identityStore.set(studentIdentity("student-2"))
        assertNull(repository.load())
        repository.connect("22222222", "ИВТ-202")

        identityStore.set(studentIdentity("student-1"))
        val firstStudentPerformance = repository.load()
        assertEquals("11••••11", firstStudentPerformance?.recordBookNumberMasked)
        assertEquals("ИВТ-101", firstStudentPerformance?.groupTitle)

        identityStore.set(studentIdentity("student-2"))
        val secondStudentPerformance = repository.refresh()
        assertEquals("22••••22", secondStudentPerformance.recordBookNumberMasked)
        assertEquals("ИВТ-202", secondStudentPerformance.groupTitle)
    }

    @Test
    fun `record book mask does not expose middle characters`() {
        assertEquals("12••••78", "12345678".maskRecordBookNumber())
        assertEquals("•••", "123".maskRecordBookNumber())
    }

    private fun repositoryForStudent(
        studentId: String,
    ): Pair<SessionIdentityStore, StubAcademicPerformanceRepository> {
        val identityStore = SessionIdentityStore().apply {
            set(studentIdentity(studentId))
        }
        return identityStore to StubAcademicPerformanceRepository(
            DemoPlatformStore(),
            identityStore,
        )
    }

    private fun studentIdentity(id: String) = SessionIdentity(
        id = id,
        displayName = "Студент $id",
        role = Role.Student,
    )
}
