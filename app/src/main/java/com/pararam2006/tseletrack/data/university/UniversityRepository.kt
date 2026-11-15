package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

interface UniversityRepository {
    suspend fun loadDashboard(): UniversityDashboard
    suspend fun confirmContract(contractId: String)
    suspend fun declineContract(contractId: String)
    suspend fun downloadDocument(documentId: String, fileName: String): DownloadedDocument
    suspend fun logout()
}
