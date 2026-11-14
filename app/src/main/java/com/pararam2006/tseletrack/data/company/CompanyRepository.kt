package com.pararam2006.tseletrack.data.company

import com.pararam2006.tseletrack.domain.role.CandidateStatus
import com.pararam2006.tseletrack.domain.role.CompanyDashboard

interface CompanyRepository {
    suspend fun loadDashboard(): CompanyDashboard
    suspend fun updateCandidateStatus(candidateId: String, status: CandidateStatus)
    suspend fun declineCandidate(candidateId: String)
    suspend fun logout()
}
