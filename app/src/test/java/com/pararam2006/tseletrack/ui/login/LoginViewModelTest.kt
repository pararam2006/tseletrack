package com.pararam2006.tseletrack.ui.login

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.AuthRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `rapid repeated login starts only one authentication request`() = runTest(dispatcher) {
        val repository = DelayedAuthRepository()
        val viewModel = LoginViewModel(repository)
        viewModel.updateEmail("student@example.test")
        viewModel.updatePassword("password")

        viewModel.login()
        viewModel.login()

        assertTrue(viewModel.uiState.value.isSubmitting)
        runCurrent()
        assertEquals(1, repository.authenticateCalls)

        repository.loginGate.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals(1, repository.authenticateCalls)
    }

    private class DelayedAuthRepository : AuthRepository {
        val loginGate = CompletableDeferred<Unit>()
        var authenticateCalls = 0

        override suspend fun authenticate(email: String, password: String): Role {
            authenticateCalls++
            loginGate.await()
            return Role.Student
        }

        override suspend fun registerStudent(
            fullName: String,
            email: String,
            password: String,
        ) = Role.Student

        override suspend fun logout() = Unit
    }
}
