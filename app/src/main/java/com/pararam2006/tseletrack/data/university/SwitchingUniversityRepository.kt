package com.pararam2006.tseletrack.data.university

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.domain.role.UniversityDashboard
import com.pararam2006.tseletrack.domain.student.DownloadedDocument

class SwitchingUniversityRepository(
    private val networkRepository: NetworkUniversityRepository,
    private val demoRepository: StubUniversityRepository,
    private val demoModeStore: DemoModeStore,
) : UniversityRepository {
    private val activeRepository: UniversityRepository
        get() = if (demoModeStore.role == Role.University) demoRepository else networkRepository

    override suspend fun loadDashboard(): UniversityDashboard = activeRepository.loadDashboard()

    override suspend fun confirmContract(contractId: String) =
        activeRepository.confirmContract(contractId)

    override suspend fun declineContract(contractId: String) =
        activeRepository.declineContract(contractId)

    override suspend fun downloadDocument(
        documentId: String,
        fileName: String,
    ): DownloadedDocument = activeRepository.downloadDocument(documentId, fileName)

    override suspend fun logout() {
        val repository = activeRepository
        demoModeStore.stopDemo()
        repository.logout()
    }
}
