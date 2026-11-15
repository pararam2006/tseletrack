package com.pararam2006.tseletrack.data.student

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StubAcademicPerformanceRepositoryTest {
    @Test
    fun `demo performance is generated without network after connection`() = runTest {
        val repository = StubAcademicPerformanceRepository()

        assertNull(repository.load())
        val performance = repository.connect("12345678", "ИВТ-221")

        assertTrue(performance.isDemo)
        assertTrue(performance.results.isNotEmpty())
        assertEquals(setOf(1, 2), performance.results.map { it.semester }.toSet())
        assertNotNull(repository.load())
    }

    @Test
    fun `record book mask does not expose middle characters`() {
        assertEquals("12••••78", "12345678".maskRecordBookNumber())
        assertEquals("•••", "123".maskRecordBookNumber())
    }
}
