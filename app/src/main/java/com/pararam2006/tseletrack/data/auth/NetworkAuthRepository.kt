package com.pararam2006.tseletrack.data.auth

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toRole
import com.pararam2006.tseletrack.data.storage.TokenStore

class NetworkAuthRepository(
    private val api: TseletrackApi,
    private val tokenStore: TokenStore,
    private val demoModeStore: DemoModeStore,
    private val demoPlatformStore: DemoPlatformStore,
) : AuthRepository {
    override suspend fun authenticate(email: String, password: String): Role {
        demoModeStore.stopDemo()
        val token = api.login(email.trim(), password)
        tokenStore.saveToken(token.accessToken)
        api.onTokenChanged()
        return try {
            api.me().toRole()
        } catch (error: Exception) {
            api.clearSession()
            throw error
        }
    }

    override suspend fun registerStudent(
        fullName: String,
        email: String,
        password: String,
    ): Role {
        demoModeStore.stopDemo()
        val normalizedEmail = email.trim().lowercase()
        api.registerStudent(
            fullName = fullName.trim(),
            email = normalizedEmail,
            password = password,
        )
        return authenticate(normalizedEmail, password)
    }

    override suspend fun startStudentDemo(): Role {
        api.clearSession()
        demoPlatformStore.reset()
        demoModeStore.startDemo(Role.Student)
        return Role.Student
    }

    override suspend fun startUniversityDemo(): Role {
        api.clearSession()
        demoModeStore.startDemo(Role.University)
        return Role.University
    }

    override suspend fun restoreSession(): Role? {
        demoModeStore.role?.let { return it }
        if (tokenStore.getToken() == null) return null
        api.onTokenChanged()
        return api.me().toRole()
    }

    override suspend fun logout() {
        demoModeStore.stopDemo()
        api.clearSession()
    }
}
