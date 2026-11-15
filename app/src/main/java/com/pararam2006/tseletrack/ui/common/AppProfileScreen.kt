package com.pararam2006.tseletrack.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

internal data class ProfileRow(
    val title: String,
    val value: String,
)

@Composable
internal fun AppProfileScreen(
    displayName: String,
    avatarLetter: String,
    role: String,
    rows: List<ProfileRow>,
    onRowClick: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        title = stringResource(R.string.profile_title),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimension.Padding.screen),
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(166.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(tseletrackRedTint, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = avatarLetter,
                            color = tseletrackRed,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
                    Text(
                        text = displayName,
                        color = tseletrackInk,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Dimension.Spacing.small))
                    Text(
                        text = role,
                        modifier = Modifier
                            .background(tseletrackRedTint, MaterialTheme.shapes.large)
                            .padding(horizontal = Dimension.Spacing.mediumLarge, vertical = Dimension.Spacing.space3),
                        color = tseletrackRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(Dimension.Spacing.largeExtra))
            rows.forEachIndexed { index, row ->
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable(role = Role.Button) { onRowClick(index) },
                    contentPadding = Dimension.Spacing.space13,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = row.title,
                            color = tseletrackInk,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = row.value,
                            color = tseletrackMut,
                            fontSize = 12.sp,
                        )
                    }
                }
                Spacer(Modifier.height(Dimension.Spacing.space9))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(tseletrackWhite, MaterialTheme.shapes.large)
                    .clickable(role = Role.Button, onClick = onLogout),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.profile_logout),
                    color = tseletrackRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
