package com.pararam2006.tseletrack.data.admin

import com.pararam2006.tseletrack.domain.role.AdminDashboard

interface AdminRepository {
    suspend fun loadDashboard(): AdminDashboard
    suspend fun exportData()
    suspend fun openRoles()
    suspend fun createBackup()
    suspend fun resetData()
    suspend fun addUser()
    suspend fun logout()
}
