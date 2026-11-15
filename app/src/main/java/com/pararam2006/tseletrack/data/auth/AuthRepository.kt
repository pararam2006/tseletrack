package com.pararam2006.tseletrack.data.auth

import com.pararam2006.tseletrack.core.Role

interface AuthRepository {
    suspend fun authenticate(email: String, password: String): Role
    suspend fun registerStudent(fullName: String, email: String, password: String): Role
    suspend fun startStudentDemo(): Role = Role.Student
    suspend fun startUniversityDemo(): Role = Role.University
    suspend fun restoreSession(): Role? = null
    suspend fun logout()
}
