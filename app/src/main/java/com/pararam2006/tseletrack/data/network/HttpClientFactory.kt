package com.pararam2006.tseletrack.data.network

import com.pararam2006.tseletrack.data.storage.TokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.clearAuthTokens
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.bodyAsText
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

internal fun createHttpClient(
    json: Json,
    tokenStore: TokenStore,
): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true

    defaultRequest {
        contentType(ContentType.Application.Json)
    }

    install(ContentNegotiation) {
        json(json)
    }
    install(Auth) {
        bearer {
            loadTokens {
                tokenStore.getToken()?.let { token -> BearerTokens(token, refreshToken = "") }
            }
            sendWithoutRequest { request -> request.url.build().encodedPath.startsWith("/api/") }
        }
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 20_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 20_000
    }
}

internal suspend inline fun <T> apiCall(
    tokenStore: TokenStore,
    crossinline block: suspend () -> T,
): T = try {
    block()
} catch (error: CancellationException) {
    throw error
} catch (error: ResponseException) {
    val status = error.response.status
    if (status == HttpStatusCode.Unauthorized) tokenStore.clear()
    val serverMessage = runCatching { error.response.bodyAsText().extractServerMessage() }.getOrNull()
    throw ApiException(
        kind = status.toErrorKind(),
        message = serverMessage ?: status.defaultMessage(),
        statusCode = status.value,
        cause = error,
    )
} catch (error: HttpRequestTimeoutException) {
    throw ApiException(ApiErrorKind.Timeout, "Сервер не ответил вовремя.", cause = error)
} catch (error: ConnectTimeoutException) {
    throw ApiException(ApiErrorKind.Timeout, "Не удалось вовремя подключиться к серверу.", cause = error)
} catch (error: SocketTimeoutException) {
    throw ApiException(ApiErrorKind.Timeout, "Сервер не ответил вовремя.", cause = error)
} catch (error: IOException) {
    throw ApiException(ApiErrorKind.Network, "Нет соединения с сервером.", cause = error)
} catch (error: SerializationException) {
    throw ApiException(ApiErrorKind.InvalidResponse, "Сервер вернул некорректные данные.", cause = error)
} catch (error: ApiException) {
    throw error
} catch (error: Exception) {
    throw ApiException(ApiErrorKind.Unknown, "Не удалось выполнить запрос.", cause = error)
}

internal fun HttpClient.resetBearerToken() {
    clearAuthTokens()
}

private fun HttpStatusCode.toErrorKind(): ApiErrorKind = when (this) {
    HttpStatusCode.Unauthorized -> ApiErrorKind.Unauthorized
    HttpStatusCode.Forbidden -> ApiErrorKind.Forbidden
    HttpStatusCode.NotFound -> ApiErrorKind.NotFound
    HttpStatusCode.Conflict -> ApiErrorKind.Conflict
    HttpStatusCode.UnprocessableEntity, HttpStatusCode.BadRequest -> ApiErrorKind.Validation
    else -> if (value >= 500) ApiErrorKind.Server else ApiErrorKind.Unknown
}

private fun HttpStatusCode.defaultMessage(): String = when (this) {
    HttpStatusCode.Unauthorized -> "Сессия истекла. Войдите снова."
    HttpStatusCode.Forbidden -> "Недостаточно прав для этого действия."
    HttpStatusCode.NotFound -> "Запрошенные данные не найдены."
    HttpStatusCode.Conflict -> "Действие конфликтует с текущим состоянием данных."
    HttpStatusCode.UnprocessableEntity, HttpStatusCode.BadRequest -> "Проверьте введённые данные."
    else -> "Ошибка сервера ($value)."
}

private fun String.extractServerMessage(): String? {
    val body = Json.parseToJsonElement(this) as? JsonObject ?: return null
    val value = body["message"] ?: body["detail"] ?: return null
    if (value is JsonPrimitive) return value.contentOrNull
    val firstError = value as? kotlinx.serialization.json.JsonArray ?: return null
    return (firstError.firstOrNull() as? JsonObject)?.get("msg")?.jsonPrimitive?.contentOrNull
}
