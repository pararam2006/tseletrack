package com.pararam2006.tseletrack.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.theme.tseletrackMut

@Composable
internal fun PasswordVisibilityButton(
    isPasswordVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(
        if (isPasswordVisible) {
            R.string.password_hide_description
        } else {
            R.string.password_show_description
        },
    )

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClickLabel = description,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                if (isPasswordVisible) {
                    R.drawable.ic_password_visibility_off
                } else {
                    R.drawable.ic_password_visibility
                },
            ),
            contentDescription = description,
            tint = tseletrackMut,
            modifier = Modifier.size(22.dp),
        )
    }
}
