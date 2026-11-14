package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NetworkStudentRepository(
    private val api: TseletrackApi,
) : StudentRepository {
    private val dashboardMutex = Mutex()
    private var cachedDashboard: StudentDashboard? = null

    override suspend fun loadDashboard(): StudentDashboard = dashboardMutex.withLock {
        cachedDashboard ?: api.studentDashboard().toDomain().also { cachedDashboard = it }
    }

    override suspend fun signContract() {
        api.signStudentContract()
        invalidateDashboard()
    }

    override suspend fun chooseUniversity(universityId: String) {
        api.selectUniversity(universityId)
        invalidateDashboard()
    }

    override suspend fun updateTask(taskId: String, isCompleted: Boolean) {
        api.updateStudentTask(taskId, isCompleted)
        invalidateDashboard()
    }

    override suspend fun addTask() {
        api.createStudentTask(DEFAULT_TASK_TITLE)
        invalidateDashboard()
    }

    override suspend fun logout() {
        invalidateDashboard()
        api.clearSession()
    }

    private suspend fun invalidateDashboard() = dashboardMutex.withLock {
        cachedDashboard = null
    }

    private companion object {
        const val DEFAULT_TASK_TITLE = "Новая задача"
    }
}
