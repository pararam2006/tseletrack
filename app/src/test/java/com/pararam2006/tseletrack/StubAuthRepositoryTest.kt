package com.pararam2006.tseletrack

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.auth.StubAuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertSame
import org.junit.Test

class StubAuthRepositoryTest {
    private val repository = StubAuthRepository()

    @Test
    fun `email prefix selects the expected role`() = runBlocking {
        assertSame(Role.Student, repository.authenticate("student@tseletrack.ru", "password"))
        assertSame(Role.University, repository.authenticate("university@tseletrack.ru", "password"))
        assertSame(Role.Company, repository.authenticate("company@tseletrack.ru", "password"))
        assertSame(Role.Admin, repository.authenticate("admin@tseletrack.ru", "password"))
    }
}
