package com.pararam2006.tseletrack.data.auth

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.UserDto
import com.pararam2006.tseletrack.data.network.dto.toRole
import com.pararam2006.tseletrack.data.session.SessionIdentity
import com.pararam2006.tseletrack.data.session.SessionIdentityStore
import com.pararam2006.tseletrack.data.storage.TokenStore

class NetworkAuthRepository(
    private val api: TseletrackApi,
    private val tokenStore: TokenStore,
    private val demoModeStore: DemoModeStore,
    private val demoPlatformStore: DemoPlatformStore,
    private val sessionIdentityStore: SessionIdentityStore,
) : AuthRepository {
    override suspend fun authenticate(email: String, password: String): Role {
        demoModeStore.stopDemo()
        sessionIdentityStore.clear()
        val token = api.login(email.trim(), password)
        tokenStore.saveToken(token.accessToken)
        return try {
            saveIdentity(api.me())
        } catch (error: Exception) {
            api.clearSession()
            sessionIdentityStore.clear()
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
        sessionIdentityStore.set(
            SessionIdentity("demo-student", "Демо-абитуриент", Role.Student),
        )
        return Role.Student
    }

    override suspend fun startUniversityDemo(): Role {
        api.clearSession()
        demoModeStore.startDemo(Role.University)
        sessionIdentityStore.set(
            SessionIdentity("demo-university", "ТулГУ", Role.University),
        )
        return Role.University
    }

    override suspend fun restoreSession(): Role? {
        demoModeStore.role?.let { role ->
            if (sessionIdentityStore.current == null) {
                sessionIdentityStore.set(role.demoIdentity())
            }
            return role
        }
        if (tokenStore.getToken() == null) {
            sessionIdentityStore.clear()
            return null
        }
        sessionIdentityStore.clear()
        return try {
            saveIdentity(api.me())
        } catch (error: ApiException) {
            if (error.kind != ApiErrorKind.Unauthorized) throw error
            api.clearSession()
            sessionIdentityStore.clear()
            null
        }
    }

    override suspend fun logout() {
        demoModeStore.stopDemo()
        api.clearSession()
        sessionIdentityStore.clear()
    }

    private fun saveIdentity(user: UserDto): Role {
        val role = user.toRole()
        sessionIdentityStore.set(
            SessionIdentity(
                id = user.id.toString(),
                displayName = user.fullName,
                role = role,
            ),
        )
        return role
    }

    private fun Role.demoIdentity(): SessionIdentity = when (this) {
        Role.Student -> SessionIdentity("demo-student", "Демо-абитуриент", this)
        Role.University -> SessionIdentity("demo-university", "ТулГУ", this)
        Role.Company -> SessionIdentity("demo-company", "ГК «Ростех»", this)
        Role.Admin -> SessionIdentity("demo-admin", "Администратор", this)
    }
}
