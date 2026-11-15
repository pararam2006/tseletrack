package com.pararam2006.tseletrack.ui.university

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.data.report.TargetOfferCsvReport
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import java.io.File
import java.time.LocalDate

internal fun Context.shareTargetOfferReport(metrics: List<TargetOfferMetric>): Boolean = runCatching {
    val reportDirectory = File(cacheDir, REPORTS_DIRECTORY).apply { mkdirs() }
    val reportFile = File(
        reportDirectory,
        "tulsu-target-offers-${LocalDate.now()}.csv",
    ).apply {
        writeText(TargetOfferCsvReport.create(metrics), Charsets.UTF_8)
    }
    val reportUri = FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        reportFile,
    )
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = CSV_MIME_TYPE
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.university_report_subject))
        putExtra(Intent.EXTRA_TEXT, getString(R.string.university_report_email_body))
        putExtra(Intent.EXTRA_STREAM, reportUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, getString(R.string.university_report_share)))
}.isSuccess

private const val REPORTS_DIRECTORY = "shared_reports"
private const val CSV_MIME_TYPE = "text/csv"
