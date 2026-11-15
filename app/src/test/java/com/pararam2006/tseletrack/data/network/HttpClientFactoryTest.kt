package com.pararam2006.tseletrack.data.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.pararam2006.tseletrack.data.storage.TokenStore
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpClientFactoryTest {
    @Test
    fun `client reads current token from store for every request`() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val tokenStore = TokenStore(dataStore)
        val authorizationHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            authorizationHeaders += request.headers[HttpHeaders.Authorization]
            respond(content = "{}", status = HttpStatusCode.OK)
        }
        val client = createHttpClient(engine, testJson(), tokenStore)

        try {
            tokenStore.saveToken("first-token")
            client.get("http://localhost/api/university/dashboard")

            tokenStore.saveToken("second-token")
            client.get("http://localhost/api/university/dashboard")

            assertEquals(
                listOf("Bearer first-token", "Bearer second-token"),
                authorizationHeaders,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `unauthorized api response does not erase persistent session`() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val tokenStore = TokenStore(dataStore)
        val engine = MockEngine {
            respond(
                content = "{\"detail\":\"Unauthorized\"}",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createHttpClient(engine, testJson(), tokenStore)
        tokenStore.saveToken("valid-session-token")

        try {
            val error = runCatching {
                apiCall { client.get("http://localhost/api/university/dashboard") }
            }.exceptionOrNull()

            assertTrue(error is ApiException)
            assertEquals(ApiErrorKind.Unauthorized, (error as ApiException).kind)
            assertEquals("valid-session-token", tokenStore.getToken())
        } finally {
            client.close()
        }
    }

    private fun testJson() = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = state

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
            transform(state.value).also { state.value = it }
    }
}
