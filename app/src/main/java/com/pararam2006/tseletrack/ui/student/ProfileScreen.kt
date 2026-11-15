package com.pararam2006.tseletrack.ui.student

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.StudentProfile
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppProfileScreen
import com.pararam2006.tseletrack.ui.common.ProfileRow

@Composable
internal fun ProfileScreen(
    isLoading: Boolean,
    profile: StudentProfile,
    isAcademicPerformanceAvailable: Boolean,
    onDocumentsClick: () -> Unit,
    onAcademicPerformanceClick: () -> Unit,
    onLogout: () -> Unit,
) {
    if (isLoading) {
        AppLoadingScreen(title = stringResource(R.string.profile_title))
        return
    }

    AppProfileScreen(
        displayName = profile.displayName,
        avatarLetter = profile.avatarLetter,
        role = profile.role,
        rows = buildList {
            add(
                ProfileRow(
                    title = stringResource(R.string.profile_documents),
                    value = pluralStringResource(
                        R.plurals.profile_document_count,
                        profile.documentCount,
                        profile.documentCount,
                    ),
                ),
            )
            if (isAcademicPerformanceAvailable) {
                add(
                    ProfileRow(
                        title = stringResource(R.string.academic_performance_title),
                        value = stringResource(R.string.profile_academic_performance_value),
                    ),
                )
            }
        },
        onRowClick = { index ->
            when (index) {
                0 -> onDocumentsClick()
                1 -> onAcademicPerformanceClick()
            }
        },
        onLogout = onLogout,
    )
}
