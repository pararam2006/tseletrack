package com.pararam2006.tseletrack.ui.common

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.pararam2006.tseletrack.domain.student.DownloadedDocument
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun Context.openDownloadedDocument(document: DownloadedDocument): Boolean = runCatching {
    val documentFile = withContext(Dispatchers.IO) {
        val directory = File(cacheDir, DOCUMENTS_DIRECTORY).apply { mkdirs() }
        File(directory, document.fileName.safeFileName()).apply {
            writeBytes(document.bytes)
        }
    }
    val documentUri = FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        documentFile,
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(documentUri, document.contentType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Открыть документ"))
}.isSuccess

private fun String.safeFileName(): String {
    val sanitized = substringAfterLast('/').substringAfterLast('\\')
        .filter { it.isLetterOrDigit() || it in " ._()-" }
        .trim()
        .take(160)
    return sanitized.ifBlank { "document.pdf" }
}

private const val DOCUMENTS_DIRECTORY = "shared_documents"
