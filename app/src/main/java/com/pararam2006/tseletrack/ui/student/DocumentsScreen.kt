package com.pararam2006.tseletrack.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.StudentDocument
import com.pararam2006.tseletrack.domain.student.StudentDocumentStatus
import com.pararam2006.tseletrack.domain.student.StudentDocumentType
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.common.AppOutlinedButton
import com.pararam2006.tseletrack.ui.common.AppScreenHeader
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint

@Composable
internal fun DocumentsScreen(
    state: DocumentsUiState,
    onUploadContract: () -> Unit,
    onOpenDocument: (String) -> Unit,
) {
    if (state.isLoading) {
        AppLoadingScreen(title = stringResource(R.string.documents_title))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppScreenHeader(title = stringResource(R.string.documents_title))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.documents_intro_title),
                        color = tseletrackInk,
                        style = AppTextStyles.sectionTitle,
                    )
                    Spacer(Modifier.height(Dimension.Spacing.small))
                    Text(
                        text = stringResource(R.string.documents_intro_description),
                        color = tseletrackMut,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            state.actionMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = if (state.isActionError) tseletrackRed else tseletrackOk,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            items(state.documents, key = StudentDocument::id) { document ->
                DocumentCard(
                    document = document,
                    isUploading = state.isUploading,
                    onUploadContract = onUploadContract,
                    onOpenDocument = { onOpenDocument(document.id) },
                )
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: StudentDocument,
    isUploading: Boolean,
    onUploadContract: () -> Unit,
    onOpenDocument: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(tseletrackRedTint, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "PDF",
                    color = tseletrackRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    color = tseletrackInk,
                    style = AppTextStyles.bodyCompactEmphasis,
                )
                document.fileName?.let { fileName ->
                    Text(
                        text = fileName,
                        color = tseletrackMut,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                    )
                }
            }
        }
        Spacer(Modifier.height(Dimension.Spacing.medium))
        DocumentStatusPill(document.status)
        Spacer(Modifier.height(Dimension.Spacing.medium))
        Text(
            text = document.description,
            color = tseletrackMut,
            style = MaterialTheme.typography.bodySmall,
        )
        document.updatedAt?.let { updatedAt ->
            Spacer(Modifier.height(Dimension.Spacing.small))
            Text(
                text = updatedAt,
                color = tseletrackMut,
                fontSize = 11.sp,
            )
        }

        if (document.downloadUrl != null && document.fileName != null) {
            Spacer(Modifier.height(Dimension.Spacing.large))
            AppOutlinedButton(
                text = stringResource(R.string.documents_open_pdf),
                onClick = onOpenDocument,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        val canUpload = document.type == StudentDocumentType.TargetContract &&
            document.status in setOf(
                StudentDocumentStatus.AwaitingUpload,
                StudentDocumentStatus.ChangesRequested,
                StudentDocumentStatus.UnderReview,
            )
        if (canUpload) {
            Spacer(Modifier.height(Dimension.Spacing.large))
            AppPrimaryButton(
                text = when {
                    isUploading -> stringResource(R.string.documents_uploading)
                    document.status == StudentDocumentStatus.UnderReview ->
                        stringResource(R.string.documents_replace_pdf)
                    else -> stringResource(R.string.documents_upload_pdf)
                },
                onClick = { if (!isUploading) onUploadContract() },
            )
        }
    }
}

@Composable
private fun DocumentStatusPill(status: StudentDocumentStatus) {
    val (background, foreground) = when (status) {
        StudentDocumentStatus.Unavailable -> tseletrackLine to tseletrackMut
        StudentDocumentStatus.AwaitingUpload -> tseletrackInfoTint to tseletrackInfo
        StudentDocumentStatus.UnderReview -> tseletrackWarnTint to tseletrackWarn
        StudentDocumentStatus.ChangesRequested -> tseletrackRedTint to tseletrackRed
        StudentDocumentStatus.Accepted,
        StudentDocumentStatus.Available,
        -> tseletrackOkTint to tseletrackOk
    }
    val label = when (status) {
        StudentDocumentStatus.Unavailable -> R.string.document_status_unavailable
        StudentDocumentStatus.AwaitingUpload -> R.string.document_status_awaiting_upload
        StudentDocumentStatus.UnderReview -> R.string.document_status_under_review
        StudentDocumentStatus.ChangesRequested -> R.string.document_status_changes_requested
        StudentDocumentStatus.Accepted -> R.string.document_status_accepted
        StudentDocumentStatus.Available -> R.string.document_status_available
    }
    Text(
        text = stringResource(label),
        modifier = Modifier
            .background(background, MaterialTheme.shapes.large)
            .padding(horizontal = Dimension.Spacing.space9, vertical = Dimension.Spacing.space3),
        color = foreground,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
    )
}
