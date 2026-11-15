package com.pararam2006.tseletrack.ui.student

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.pararam2006.tseletrack.domain.student.StudentDocumentUpload
import com.pararam2006.tseletrack.domain.student.StudentDocumentContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal fun ContentResolver.readDocumentUpload(uri: Uri): StudentDocumentUpload {
    val resolver = this
    var displayName: String? = null
    var sizeBytes: Long? = null
    query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIndex >= 0) displayName = cursor.getString(nameIndex)
            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) sizeBytes = cursor.getLong(sizeIndex)
        }
    }
    return StudentDocumentUpload(
        fileName = displayName?.takeIf(String::isNotBlank)
            ?: uri.lastPathSegment?.substringAfterLast('/')
            ?: "Договор.pdf",
        mimeType = getType(uri),
        sizeBytes = sizeBytes,
        content = StudentDocumentContent {
            withContext(Dispatchers.IO) {
                resolver.openInputStream(uri)?.use { it.readBytesLimited(MAX_PICKED_FILE_BYTES) }
                    ?: error("Не удалось открыть выбранный PDF")
            }
        },
    )
}

private fun InputStream.readBytesLimited(maxBytes: Int): ByteArray {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var total = 0
    while (true) {
        val count = read(buffer)
        if (count < 0) break
        total += count
        check(total <= maxBytes) { "Размер файла не должен превышать 10 МБ" }
        output.write(buffer, 0, count)
    }
    return output.toByteArray()
}

private const val MAX_PICKED_FILE_BYTES = 10 * 1024 * 1024
