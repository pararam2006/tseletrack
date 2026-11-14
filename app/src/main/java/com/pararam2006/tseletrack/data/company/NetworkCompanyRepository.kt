package com.pararam2006.tseletrack.data.company

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toApiValue
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard

class NetworkCompanyRepository(
    private val api: TseletrackApi,
) : CompanyRepository {
    override suspend fun loadDashboard(): CompanyDashboard = api.companyDashboard().toDomain()

    override suspend fun updateCandidateStatus(candidateId: String, status: CandidateStatus) {
        api.updateCandidate(candidateId, status.toApiValue())
    }

    override suspend fun declineCandidate(candidateId: String) {
        api.declineCandidate(candidateId)
    }

    override suspend fun logout() {
        api.clearSession()
    }
}
