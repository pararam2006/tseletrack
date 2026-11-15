package com.pararam2006.tseletrack.ui.session

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `logout clears session before navigation event`() = runTest(dispatcher) {
        val repository = DelayedLogoutAuthRepository()
        val viewModel = SessionViewModel(repository)
        advanceUntilIdle()
        viewModel.onAuthenticated(Role.Student)

        viewModel.logout()
        runCurrent()

        assertTrue(repository.logoutStarted.isCompleted)
        assertTrue(viewModel.uiState.value.isLoggingOut)
        assertEquals(Role.Student, viewModel.uiState.value.role)

        repository.allowLogout.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoggingOut)
        assertEquals(null, viewModel.uiState.value.role)
        assertEquals(Unit, viewModel.logoutCompleted.first())
    }

    private class DelayedLogoutAuthRepository : AuthRepository {
        val logoutStarted = CompletableDeferred<Unit>()
        val allowLogout = CompletableDeferred<Unit>()

        override suspend fun authenticate(email: String, password: String) = Role.Student

        override suspend fun registerStudent(fullName: String, email: String, password: String) =
            Role.Student

        override suspend fun restoreSession(): Role? = null

        override suspend fun logout() {
            logoutStarted.complete(Unit)
            allowLogout.await()
        }
    }
}
