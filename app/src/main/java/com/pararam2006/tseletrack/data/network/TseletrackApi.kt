package com.pararam2006.tseletrack.data.network

import com.pararam2006.tseletrack.data.network.dto.AdminDashboardDto
import com.pararam2006.tseletrack.data.network.dto.CandidateDto
import com.pararam2006.tseletrack.data.network.dto.CandidateStatusRequestDto
import com.pararam2006.tseletrack.data.network.dto.CompanyDashboardDto
import com.pararam2006.tseletrack.data.network.dto.ContractDto
import com.pararam2006.tseletrack.data.network.dto.LoginRequestDto
import com.pararam2006.tseletrack.data.network.dto.AdmissionStatusDto
import com.pararam2006.tseletrack.data.network.dto.AdmissionTrackingRequestDto
import com.pararam2006.tseletrack.data.network.dto.RegisterRequestDto
import com.pararam2006.tseletrack.data.network.dto.StudentDashboardDto
import com.pararam2006.tseletrack.data.network.dto.StudentTaskDto
import com.pararam2006.tseletrack.data.network.dto.RouteStageDto
import com.pararam2006.tseletrack.data.network.dto.StageUpdateRequestDto
import com.pararam2006.tseletrack.data.network.dto.TaskCreateRequestDto
import com.pararam2006.tseletrack.data.network.dto.TaskUpdateRequestDto
import com.pararam2006.tseletrack.data.network.dto.TokenResponseDto
import com.pararam2006.tseletrack.data.network.dto.TargetOfferListDto
import com.pararam2006.tseletrack.data.network.dto.TargetOfferSelectionDto
import com.pararam2006.tseletrack.data.network.dto.UniversityDashboardDto
import com.pararam2006.tseletrack.data.network.dto.UserDto
import com.pararam2006.tseletrack.data.storage.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class TseletrackApi(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
    baseUrl: String,
) {
    private val baseUrl = baseUrl.trimEnd('/')

    suspend fun login(email: String, password: String): TokenResponseDto = call {
        client.post(url("api/auth/login")) {
            setBody(LoginRequestDto(email = email, password = password))
        }.body()
    }

    suspend fun registerStudent(
        fullName: String,
        email: String,
        password: String,
    ): UserDto = call {
        client.post(url("api/auth/register")) {
            setBody(
                RegisterRequestDto(
                    email = email,
                    password = password,
                    fullName = fullName,
                ),
            )
        }.body()
    }

    suspend fun me(): UserDto = call { client.get(url("api/auth/me")).body() }

    suspend fun studentDashboard(): StudentDashboardDto = call {
        client.get(url("api/student/dashboard")).body()
    }

    suspend fun signStudentContract(): ContractDto = call {
        client.post(url("api/student/contract/sign")).body()
    }

    suspend fun targetOffers(forceRefresh: Boolean): TargetOfferListDto = call {
        client.get(url("api/student/target-offers")) {
            parameter("refresh", forceRefresh)
        }.body()
    }

    suspend fun targetOfferSelection(): TargetOfferSelectionDto = call {
        client.get(url("api/student/target-offers/selection")).body()
    }

    suspend fun selectTargetOffer(offerId: String): TargetOfferSelectionDto = call {
        client.put(url("api/student/target-offers/$offerId/selection")).body()
    }

    suspend fun admissionStatus(): AdmissionStatusDto = call {
        client.get(url("api/student/admission-status")).body()
    }

    suspend fun saveApplicantCode(applicantCode: String): AdmissionStatusDto = call {
        client.put(url("api/student/admission-tracking")) {
            setBody(AdmissionTrackingRequestDto(applicantCode))
        }.body()
    }

    suspend fun refreshAdmissionStatus(): AdmissionStatusDto = call {
        client.post(url("api/student/admission-status/refresh")) {
            timeout {
                requestTimeoutMillis = ADMISSION_REFRESH_TIMEOUT_MILLIS
                socketTimeoutMillis = ADMISSION_REFRESH_TIMEOUT_MILLIS
            }
        }.body()
    }

    suspend fun updateStudentTask(taskId: String, isCompleted: Boolean): StudentTaskDto = call {
        client.patch(url("api/student/tasks/$taskId")) {
            setBody(TaskUpdateRequestDto(isCompleted))
        }.body()
    }

    suspend fun createStudentTask(title: String): StudentTaskDto = call {
        client.post(url("api/student/tasks")) {
            setBody(TaskCreateRequestDto(title))
        }.body()
    }

    suspend fun updateRouteStage(stageId: String, status: String): RouteStageDto = call {
        client.patch(url("api/stages/$stageId")) {
            setBody(StageUpdateRequestDto(status))
        }.body()
    }

    suspend fun universityDashboard(): UniversityDashboardDto = call {
        client.get(url("api/university/dashboard")).body()
    }

    suspend fun confirmUniversityContract(contractId: String): ContractDto = call {
        client.post(url("api/university/contracts/$contractId/confirm")).body()
    }

    suspend fun declineUniversityContract(contractId: String): ContractDto = call {
        client.post(url("api/university/contracts/$contractId/decline")).body()
    }

    suspend fun companyDashboard(): CompanyDashboardDto = call {
        client.get(url("api/company/dashboard")).body()
    }

    suspend fun updateCandidate(routeId: String, status: String): CandidateDto = call {
        client.patch(url("api/company/candidates/$routeId")) {
            setBody(CandidateStatusRequestDto(status))
        }.body()
    }

    suspend fun declineCandidate(routeId: String): CandidateDto = call {
        client.post(url("api/company/candidates/$routeId/decline")).body()
    }

    suspend fun adminDashboard(): AdminDashboardDto = call {
        client.get(url("api/admin/dashboard")).body()
    }

    suspend fun exportAdminData(): ByteArray = call {
        client.get(url("api/admin/export")).body()
    }

    suspend fun onTokenChanged() {
        client.resetBearerToken()
    }

    suspend fun clearSession() {
        tokenStore.clear()
        client.resetBearerToken()
    }

    private suspend inline fun <T> call(crossinline block: suspend () -> T): T =
        apiCall(tokenStore, block)

    private fun url(path: String): String = "$baseUrl/${path.trimStart('/')}"

    private companion object {
        const val ADMISSION_REFRESH_TIMEOUT_MILLIS = 120_000L
    }
}
