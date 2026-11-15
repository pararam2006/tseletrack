package com.pararam2006.tseletrack.data.auth

import com.pararam2006.tseletrack.core.Role

/** Temporary role resolver. Replace with the role returned by the authentication API. */
class StubAuthRepository : AuthRepository {
    override suspend fun authenticate(email: String, password: String): Role {
        val account = email.substringBefore('@').trim().lowercase()
        return when {
            account.startsWith("admin") -> Role.Admin
            account.startsWith("company") || account.startsWith("employer") -> Role.Company
            account.startsWith("university") || account.startsWith("uni") -> Role.University
            else -> Role.Student
        }
    }


    override suspend fun registerStudent(
        fullName: String,
        email: String,
        password: String,
    ): Role = Role.Student

    override suspend fun logout() = Unit
}
