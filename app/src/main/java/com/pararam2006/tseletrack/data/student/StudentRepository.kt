package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.StudentDashboard

interface StudentRepository {
    suspend fun loadDashboard(): StudentDashboard
    suspend fun signContract()
    suspend fun chooseUniversity(universityId: String)
    suspend fun updateTask(taskId: String, isCompleted: Boolean)
    suspend fun addTask()
    suspend fun logout()
}
