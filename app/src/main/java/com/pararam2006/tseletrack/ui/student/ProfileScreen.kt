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
    onSettingClick: (ProfileSetting) -> Unit,
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
        rows = listOf(
            ProfileRow(
                title = stringResource(R.string.profile_theme),
                value = stringResource(R.string.profile_theme_light),
            ),
            ProfileRow(
                title = stringResource(R.string.profile_notifications),
                value = stringResource(R.string.profile_notifications_on),
            ),
            ProfileRow(
                title = stringResource(R.string.profile_documents),
                value = pluralStringResource(
                    R.plurals.profile_document_count,
                    profile.documentCount,
                    profile.documentCount,
                ),
            ),
        ),
        onRowClick = { index ->
            val setting = when (index) {
                0 -> ProfileSetting.Theme
                1 -> ProfileSetting.Notifications
                else -> ProfileSetting.Documents
            }
            onSettingClick(setting)
        },
        onLogout = onLogout,
    )
}
