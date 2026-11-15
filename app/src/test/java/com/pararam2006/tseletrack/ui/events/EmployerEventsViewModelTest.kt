package com.pararam2006.tseletrack.ui.events

import com.pararam2006.tseletrack.data.events.EmployerEventsException
import com.pararam2006.tseletrack.data.events.EmployerEventsRepository
import com.pararam2006.tseletrack.domain.events.EmployerEvent
import com.pararam2006.tseletrack.domain.events.EmployerEventFormat
import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.EmployerEventType
import com.pararam2006.tseletrack.domain.events.ManagedEmployerEvent
import java.time.LocalDateTime
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
class EmployerEventsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `latest student organization wins over an unfinished load`() = runTest(dispatcher) {
        val repository = FakeEmployerEventsRepository().apply {
            studentLoads["Первая"] = CompletableDeferred()
            studentResults["Вторая"] = listOf(summary("second", "Вторая"))
        }
        val viewModel = StudentEmployerEventsViewModel(repository)

        viewModel.load("Первая")
        runCurrent()
        viewModel.load("Вторая")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Вторая", state.organizationName)
        assertEquals(listOf("second"), state.events.map { it.event.id })
        assertFalse(state.isLoading)
    }

    @Test
    fun `second tap is ignored while registration is pending`() = runTest(dispatcher) {
        val event = summary("event", "Сплав")
        val repository = FakeEmployerEventsRepository().apply {
            studentResults["Сплав"] = listOf(event)
            registrationGate = CompletableDeferred()
            registrationResult = event.copy(isRegistered = true, registeredCount = 2)
        }
        val viewModel = StudentEmployerEventsViewModel(repository)
        viewModel.load("Сплав")
        advanceUntilIdle()

        viewModel.toggleRegistration("event")
        viewModel.toggleRegistration("event")

        assertEquals("event", viewModel.uiState.value.updatingEventId)
        runCurrent()
        assertEquals(1, repository.registrationCalls)

        repository.registrationGate?.complete(Unit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.events.single().isRegistered)
        assertNull(viewModel.uiState.value.updatingEventId)
    }

    @Test
    fun `organization change cancels registration result from previous screen`() = runTest(dispatcher) {
        val first = summary("first", "Первая")
        val second = summary("second", "Вторая")
        val repository = FakeEmployerEventsRepository().apply {
            studentResults["Первая"] = listOf(first)
            studentResults["Вторая"] = listOf(second)
            registrationGate = CompletableDeferred()
            registrationResult = first.copy(isRegistered = true)
        }
        val viewModel = StudentEmployerEventsViewModel(repository)
        viewModel.load("Первая")
        advanceUntilIdle()
        viewModel.toggleRegistration("first")
        runCurrent()

        viewModel.load("Вторая")
        repository.registrationGate?.complete(Unit)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Вторая", state.organizationName)
        assertEquals(listOf("second"), state.events.map { it.event.id })
        assertNull(state.updatingEventId)
        assertNull(state.message)
    }

    @Test
    fun `expected registration error is shown without generic replacement`() = runTest(dispatcher) {
        val repository = FakeEmployerEventsRepository().apply {
            studentResults["Сплав"] = listOf(summary("event", "Сплав"))
            registrationError = EmployerEventsException("Свободных мест больше нет")
        }
        val viewModel = StudentEmployerEventsViewModel(repository)
        viewModel.load("Сплав")
        advanceUntilIdle()

        viewModel.toggleRegistration("event")
        advanceUntilIdle()

        assertEquals("Свободных мест больше нет", viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.updatingEventId)
    }

    @Test
    fun `latest employer organization wins over an unfinished load`() = runTest(dispatcher) {
        val repository = FakeEmployerEventsRepository().apply {
            employerLoads["Первая"] = CompletableDeferred()
            employerResults["Вторая"] = listOf(managed("second", "Вторая"))
        }
        val viewModel = CompanyEventsViewModel(repository)

        viewModel.load("Первая")
        runCurrent()
        viewModel.load("Вторая")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Вторая", state.organizationName)
        assertEquals(listOf("second"), state.events.map { it.event.id })
        assertFalse(state.isLoading)
    }

    private class FakeEmployerEventsRepository : EmployerEventsRepository {
        val studentLoads = mutableMapOf<String, CompletableDeferred<Unit>>()
        val employerLoads = mutableMapOf<String, CompletableDeferred<Unit>>()
        val studentResults = mutableMapOf<String, List<EmployerEventSummary>>()
        val employerResults = mutableMapOf<String, List<ManagedEmployerEvent>>()
        var registrationGate: CompletableDeferred<Unit>? = null
        var registrationResult: EmployerEventSummary? = null
        var registrationError: Exception? = null
        var registrationCalls = 0

        override suspend fun loadForStudent(
            organizationName: String,
        ): List<EmployerEventSummary> {
            studentLoads[organizationName]?.await()
            return studentResults[organizationName].orEmpty()
        }

        override suspend fun setRegistration(
            eventId: String,
            isRegistered: Boolean,
        ): EmployerEventSummary {
            registrationCalls++
            registrationGate?.await()
            registrationError?.let { throw it }
            return checkNotNull(registrationResult ?: summary(eventId, "Сплав"))
        }

        override suspend fun loadForEmployer(
            organizationName: String,
        ): List<ManagedEmployerEvent> {
            employerLoads[organizationName]?.await()
            return employerResults[organizationName].orEmpty()
        }
    }

    private companion object {
        fun event(id: String, organization: String) = EmployerEvent(
            id = id,
            organizationName = organization,
            title = "Мероприятие $id",
            type = EmployerEventType.Excursion,
            format = EmployerEventFormat.Offline,
            startsAt = LocalDateTime.of(2026, 11, 20, 12, 0),
            location = "Тула",
            description = "Описание",
            capacity = 30,
        )

        fun summary(id: String, organization: String) = EmployerEventSummary(
            event = event(id, organization),
            registeredCount = 1,
            isRegistered = false,
        )

        fun managed(id: String, organization: String) = ManagedEmployerEvent(
            event = event(id, organization),
            registeredCount = 1,
            participants = emptyList(),
        )
    }
}
