package com.pararam2006.tseletrack.data.network

import com.pararam2006.tseletrack.data.network.dto.AdminDashboardDto
import com.pararam2006.tseletrack.data.network.dto.CandidateDto
import com.pararam2006.tseletrack.data.network.dto.CandidateStatusRequestDto
import com.pararam2006.tseletrack.data.network.dto.CompanyDashboardDto
import com.pararam2006.tseletrack.data.network.dto.ContractDto
import com.pararam2006.tseletrack.data.network.dto.LoginRequestDto
import com.pararam2006.tseletrack.data.network.dto.RegisterRequestDto
import com.pararam2006.tseletrack.data.network.dto.StudentDashboardDto
import com.pararam2006.tseletrack.data.network.dto.StudentTaskDto
import com.pararam2006.tseletrack.data.network.dto.TaskCreateRequestDto
import com.pararam2006.tseletrack.data.network.dto.TaskUpdateRequestDto
import com.pararam2006.tseletrack.data.network.dto.TokenResponseDto
import com.pararam2006.tseletrack.data.network.dto.UniversityDashboardDto
import com.pararam2006.tseletrack.data.network.dto.UniversitySelectionDto
import com.pararam2006.tseletrack.data.network.dto.UserDto
import com.pararam2006.tseletrack.data.storage.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
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

    suspend fun selectUniversity(universityId: String): UniversitySelectionDto = call {
        client.put(url("api/student/universities/$universityId/selection")).body()
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
}
