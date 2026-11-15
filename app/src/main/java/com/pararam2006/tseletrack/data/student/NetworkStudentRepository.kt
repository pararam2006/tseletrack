package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.StudentDashboard
import com.pararam2006.tseletrack.domain.student.RouteStageStatus
import com.pararam2006.tseletrack.domain.student.RouteStageType
import com.pararam2006.tseletrack.domain.student.TargetOfferCatalog
import com.pararam2006.tseletrack.domain.student.TargetOfferSelection
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

    override suspend fun loadTargetOffers(forceRefresh: Boolean): TargetOfferCatalog =
        api.targetOffers(forceRefresh).toDomain()

    override suspend fun loadTargetOfferSelection(): TargetOfferSelection? =
        notFoundAsNull { api.targetOfferSelection().toDomain() }

    override suspend fun chooseTargetOffer(offerId: String): TargetOfferSelection =
        api.selectTargetOffer(offerId).toDomain()

    override suspend fun loadAdmissionStatus(): AdmissionStatus? =
        notFoundAsNull { api.admissionStatus().toDomain() }

    override suspend fun saveApplicantCode(applicantCode: String): AdmissionStatus =
        api.saveApplicantCode(applicantCode).toDomain()

    override suspend fun refreshAdmissionStatus(): AdmissionStatus {
        val status = api.refreshAdmissionStatus().toDomain()
        invalidateDashboard()
        return status
    }

    override suspend fun updateTask(taskId: String, isCompleted: Boolean) {
        api.updateStudentTask(taskId, isCompleted)
        invalidateDashboard()
    }

    override suspend fun addTask() {
        api.createStudentTask(DEFAULT_TASK_TITLE)
        invalidateDashboard()
    }

    override suspend fun completeCareerGuidance() {
        val careerStage = loadDashboard().routeStages.firstOrNull { stage ->
            stage.type == RouteStageType.CareerGuidance
        } ?: error("Этап профориентации не найден")
        if (careerStage.status == RouteStageStatus.Completed) return

        api.updateRouteStage(careerStage.id, COMPLETED_STATUS)
        invalidateDashboard()
    }

    override suspend fun logout() {
        invalidateDashboard()
        api.clearSession()
    }

    private suspend fun invalidateDashboard() = dashboardMutex.withLock {
        cachedDashboard = null
    }

    private suspend fun <T> notFoundAsNull(block: suspend () -> T): T? = try {
        block()
    } catch (error: ApiException) {
        if (error.kind == ApiErrorKind.NotFound) null else throw error
    }

    private companion object {
        const val DEFAULT_TASK_TITLE = "Новая задача"
        const val COMPLETED_STATUS = "completed"
    }
}
