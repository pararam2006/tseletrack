package com.pararam2006.tseletrack.data.network

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiExceptionTest {
    @Test
    fun `api exception exposes safe user message`() {
        val error = ApiException(ApiErrorKind.Network, "Нет соединения с сервером.")

        assertEquals("Нет соединения с сервером.", error.userMessage())
    }

    @Test
    fun `unexpected exception does not leak implementation details`() {
        val error = IllegalStateException("secret internal detail")

        assertEquals("Не удалось выполнить запрос. Попробуйте ещё раз.", error.userMessage())
    }
}
