package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

class NetworkUniversityRepository(
    private val api: TseletrackApi,
) : UniversityRepository {
    override suspend fun loadDashboard(): UniversityDashboard = api.universityDashboard().toDomain()

    override suspend fun confirmContract(contractId: String) {
        api.confirmUniversityContract(contractId)
    }

    override suspend fun declineContract(contractId: String) {
        api.declineUniversityContract(contractId)
    }

    override suspend fun downloadDocument(
        documentId: String,
        fileName: String,
    ): DownloadedDocument = api.downloadDocument(documentId, fileName)

    override suspend fun logout() {
        api.clearSession()
    }
}
