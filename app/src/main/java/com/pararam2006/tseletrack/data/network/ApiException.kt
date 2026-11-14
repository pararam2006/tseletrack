package com.pararam2006.tseletrack.data.network

enum class ApiErrorKind {
    Unauthorized,
    Forbidden,
    NotFound,
    Validation,
    Conflict,
    Server,
    Timeout,
    Network,
    InvalidResponse,
    Unknown,
}

class ApiException(
    val kind: ApiErrorKind,
    override val message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

fun Throwable.userMessage(): String = when (this) {
    is ApiException -> message
    else -> "Не удалось выполнить запрос. Попробуйте ещё раз."
}
