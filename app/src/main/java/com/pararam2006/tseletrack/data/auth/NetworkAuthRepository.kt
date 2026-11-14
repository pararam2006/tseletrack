package com.pararam2006.tseletrack.data.auth

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toRole
import com.pararam2006.tseletrack.data.storage.TokenStore

class NetworkAuthRepository(
    private val api: TseletrackApi,
    private val tokenStore: TokenStore,
) : AuthRepository {
    override suspend fun authenticate(email: String, password: String): Role {
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
        val normalizedEmail = email.trim().lowercase()
        api.registerStudent(
            fullName = fullName.trim(),
            email = normalizedEmail,
            password = password,
        )
        return authenticate(normalizedEmail, password)
    }

    override suspend fun restoreSession(): Role? {
        if (tokenStore.getToken() == null) return null
        api.onTokenChanged()
        return api.me().toRole()
    }
}
