package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.TargetOffer
import java.net.URI

internal data class TargetOfferExternalLinks(
    val applicationUrl: String?,
    val sourceUrl: String?,
    val shouldShowSourceLink: Boolean,
)

/**
 * Keeps external navigation decisions independent from Compose and Android's [android.net.Uri].
 * The backend is an external-data adapter, so every URL still has to be treated as untrusted input.
 */
internal fun TargetOffer.externalLinks(): TargetOfferExternalLinks {
    val safeSourceUrl = sourceUrl.toSafeExternalUrlOrNull()
    val safeOfficialUrl = officialApplicationUrl.toSafeExternalUrlOrNull()
    val applicationUrl = safeOfficialUrl ?: safeSourceUrl
    val shouldShowSourceLink = applicationUrl?.isGosuslugiUrl() == true &&
        safeSourceUrl != null &&
        applicationUrl.externalUrlKey() != safeSourceUrl.externalUrlKey()

    return TargetOfferExternalLinks(
        applicationUrl = applicationUrl,
        sourceUrl = safeSourceUrl,
        shouldShowSourceLink = shouldShowSourceLink,
    )
}

internal fun String.toSafeExternalUrlOrNull(): String? {
    val value = trim()
    if (value.isEmpty()) return null

    val uri = runCatching { URI(value) }.getOrNull() ?: return null
    val scheme = uri.scheme?.lowercase() ?: return null
    if (scheme !in ALLOWED_EXTERNAL_SCHEMES) return null
    if (uri.host.isNullOrBlank() || uri.userInfo != null) return null

    return value
}

private fun String.isGosuslugiUrl(): Boolean {
    val host = URI(this).host.lowercase()
    return host == GOSUSLUGI_HOST || host.endsWith(".$GOSUSLUGI_HOST")
}

private fun String.externalUrlKey(): String {
    val uri = URI(this)
    val path = uri.rawPath.orEmpty().trimEnd('/')
    return buildString {
        append(uri.scheme.lowercase())
        append("://")
        append(uri.host.lowercase())
        if (uri.port != -1) append(":${uri.port}")
        append(path)
        uri.rawQuery?.let { append("?$it") }
        uri.rawFragment?.let { append("#$it") }
    }
}

private val ALLOWED_EXTERNAL_SCHEMES = setOf("http", "https")
private const val GOSUSLUGI_HOST = "gosuslugi.ru"
