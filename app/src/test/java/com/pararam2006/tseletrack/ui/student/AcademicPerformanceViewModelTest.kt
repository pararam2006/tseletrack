package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.data.student.AcademicPerformanceRepository
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import com.pararam2006.tseletrack.domain.student.AcademicResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AcademicPerformanceViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `missing tracking opens connection form`() = runTest(dispatcher) {
        val viewModel = AcademicPerformanceViewModel(FakeRepository(initial = null))

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.performance)
    }

    @Test
    fun `invalid input is rejected before repository call`() = runTest(dispatcher) {
        val repository = FakeRepository(initial = null)
        val viewModel = AcademicPerformanceViewModel(repository)

        viewModel.updateRecordBookNumber("1")
        viewModel.updateGroupTitle("A")
        viewModel.connect()

        assertEquals(0, repository.connectCalls)
        assertTrue(viewModel.uiState.value.isMessageError)
    }

    @Test
    fun `valid tracking loads academic results`() = runTest(dispatcher) {
        val repository = FakeRepository(initial = null)
        val viewModel = AcademicPerformanceViewModel(repository)

        viewModel.updateRecordBookNumber("12345678")
        viewModel.updateGroupTitle("ИВТ-221")
        viewModel.connect()

        assertEquals(1, repository.connectCalls)
        assertEquals("ИВТ-221", viewModel.uiState.value.performance?.groupTitle)
        assertFalse(viewModel.uiState.value.isMessageError)
    }

    @Test
    fun `refresh error preserves last successful results`() = runTest(dispatcher) {
        val repository = FakeRepository(initial = performance, refreshError = true)
        val viewModel = AcademicPerformanceViewModel(repository)

        viewModel.refresh()

        assertEquals(performance, viewModel.uiState.value.performance)
        assertTrue(viewModel.uiState.value.isMessageError)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `tracking can be edited and cancelled without losing results`() = runTest(dispatcher) {
        val viewModel = AcademicPerformanceViewModel(FakeRepository(initial = performance))

        viewModel.editTracking()

        assertTrue(viewModel.uiState.value.isEditingTracking)
        assertEquals("ИВТ-221", viewModel.uiState.value.groupTitle)
        assertEquals(performance, viewModel.uiState.value.performance)

        viewModel.cancelEditing()

        assertFalse(viewModel.uiState.value.isEditingTracking)
        assertEquals(performance, viewModel.uiState.value.performance)
    }

    @Test
    fun `account reload replaces previous student academic data`() = runTest(dispatcher) {
        val repository = FakeRepository(initial = performance)
        val viewModel = AcademicPerformanceViewModel(repository)
        repository.initial = null

        viewModel.reloadForActiveUser()

        assertNull(viewModel.uiState.value.performance)
        assertEquals("", viewModel.uiState.value.recordBookNumber)
        assertEquals("", viewModel.uiState.value.groupTitle)
    }

    private class FakeRepository(
        var initial: AcademicPerformance?,
        private val refreshError: Boolean = false,
    ) : AcademicPerformanceRepository {
        var connectCalls = 0

        override suspend fun load(): AcademicPerformance? = initial

        override suspend fun connect(
            recordBookNumber: String,
            groupTitle: String,
        ): AcademicPerformance {
            connectCalls += 1
            return performance.copy(groupTitle = groupTitle)
        }

        override suspend fun refresh(): AcademicPerformance {
            if (refreshError) error("Unavailable")
            return performance
        }
    }

    private companion object {
        val performance = AcademicPerformance(
            recordBookNumberMasked = "12••••78",
            groupTitle = "ИВТ-221",
            results = listOf(
                AcademicResult(1, "Физика", "80", "Хорошо", null, null, false),
            ),
            fetchedAt = "15.11",
            isStale = false,
            warning = null,
            sourceUrl = "https://tulsu.ru/progress/",
        )
    }
}
