package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.domain.role.UniversityDashboard

interface UniversityRepository {
    suspend fun loadDashboard(): UniversityDashboard
    suspend fun confirmContract(contractId: String)
    suspend fun declineContract(contractId: String)
    suspend fun logout()
}
