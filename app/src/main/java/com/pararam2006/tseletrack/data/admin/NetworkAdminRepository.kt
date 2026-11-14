package com.pararam2006.tseletrack.data.admin

import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.role.AdminDashboard

class NetworkAdminRepository(
    private val api: TseletrackApi,
) : AdminRepository {
    override suspend fun loadDashboard(): AdminDashboard = api.adminDashboard().toDomain()

    override suspend fun exportData() {
        api.exportAdminData()
    }

    // These actions intentionally stay local until the backend exposes safe, audited endpoints.
    override suspend fun openRoles() = Unit
    override suspend fun createBackup() = Unit
    override suspend fun resetData() = Unit
    override suspend fun addUser() = Unit

    override suspend fun logout() {
        api.clearSession()
    }
}
