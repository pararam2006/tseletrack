package com.pararam2006.tseletrack.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.ui.theme.AppShapes
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite

@Composable
internal fun AppScreen(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppScreenHeader(title = title)
        content()
    }
}

@Composable
internal fun AppScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(tseletrackWhite)
            .statusBarsPadding()
            .height(Dimension.Size.header)
            .padding(
                horizontal = Dimension.Padding.screen,
                vertical = Dimension.Spacing.mediumLarge,
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
        Text(
            text = title,
            color = tseletrackInk,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
internal fun AppBackScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(tseletrackWhite)
            .statusBarsPadding()
            .height(Dimension.Size.header)
            .padding(horizontal = Dimension.Padding.screen),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back),
            modifier = Modifier
                .size(Dimension.Size.button)
                .clickable(role = Role.Button, onClick = onBack)
                .padding(Dimension.Spacing.mediumLarge),
            tint = tseletrackInk,
        )
        Spacer(Modifier.width(Dimension.Spacing.small))
        Text(
            text = title,
            color = tseletrackInk,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
internal fun AppLoadingScreen(title: String) {
    AppScreen(title = title) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = tseletrackRed)
        }
    }
}

@Composable
internal fun AppErrorScreen(
    title: String,
    message: String,
    onRetry: () -> Unit,
) {
    AppScreen(title = title) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(Dimension.Spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = message,
                color = tseletrackMut,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(Dimension.Spacing.extraLarge))
            AppPrimaryButton(
                text = "Повторить",
                onClick = onRetry,
                modifier = Modifier.width(180.dp),
            )
        }
    }
}

@Composable
internal fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = Dimension.Padding.card,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = tseletrackWhite,
        border = BorderStroke(Dimension.Size.stroke, tseletrackLine),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Composable
internal fun AppProgressBar(
    percent: Int,
    modifier: Modifier = Modifier,
    color: Color = tseletrackRed,
    height: Dp = Dimension.Size.progress,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(tseletrackLine, RoundedCornerShape(height)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                .background(color, RoundedCornerShape(height)),
        )
    }
}

@Composable
internal fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimension.Size.button)
            .background(
                if (enabled) tseletrackRed else tseletrackLine,
                MaterialTheme.shapes.medium,
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) tseletrackWhite else tseletrackMut,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
internal fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .height(Dimension.Size.outlinedButton)
            .border(
                Dimension.Size.stroke,
                if (enabled) tseletrackLine else tseletrackLine.copy(alpha = 0.6f),
                AppShapes.outlinedButton,
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Dimension.Padding.buttonHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) tseletrackInk else tseletrackMut,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
    }
}

@Composable
internal fun AppInlineNotice(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val background = if (isError) tseletrackRedTint else tseletrackOkTint
    val foreground = if (isError) tseletrackRed else tseletrackOk
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background, MaterialTheme.shapes.medium)
            .padding(Dimension.Spacing.large),
    ) {
        Text(
            text = message,
            color = foreground,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
internal fun AppSearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimension.Size.input)
            .background(tseletrackWhite, AppShapes.pill)
            .border(Dimension.Size.stroke, tseletrackLine, AppShapes.pill),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = tseletrackInk),
        cursorBrush = SolidColor(tseletrackRed),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimension.Padding.screen),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchIcon()
                Spacer(Modifier.width(Dimension.Spacing.medium))
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = tseletrackMut,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

@Composable
internal fun AppFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(Dimension.Size.chip)
            .background(
                color = if (isSelected) tseletrackInk else tseletrackWhite,
                shape = AppShapes.pill,
            )
            .border(Dimension.Size.stroke, tseletrackLine, AppShapes.pill)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Dimension.Spacing.space13),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (isSelected) tseletrackWhite else tseletrackInk,
            style = AppTextStyles.filterLabel,
            maxLines = 1,
        )
    }
}

@Composable
internal fun AppMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = tseletrackInk,
) {
    AppCard(
        modifier = modifier.height(Dimension.Size.metricCard),
        contentPadding = Dimension.Spacing.large,
    ) {
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = label,
            color = tseletrackMut,
            style = AppTextStyles.metricLabel,
        )
    }
}

@Composable
internal fun AppFloatingButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(Dimension.Size.floatingButton)
            .background(if (enabled) tseletrackRed else tseletrackLine, AppShapes.floatingButton)
            .semantics { this.contentDescription = contentDescription }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = tseletrackWhite,
            style = AppTextStyles.floatingAction,
        )
    }
}

internal data class AppBottomTab<T>(
    val value: T,
    @get:StringRes val labelRes: Int,
    @get:DrawableRes val iconRes: Int,
    val testTag: String,
)

@Composable
internal fun <T> AppBottomBar(
    tabs: List<AppBottomTab<T>>,
    selectedTab: T,
    onTabSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = tseletrackWhite,
        border = BorderStroke(Dimension.Size.stroke, tseletrackLine),
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .height(Dimension.Size.bottomBar)
                .padding(horizontal = Dimension.Spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                val selected = tab.value == selectedTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(tab.testTag)
                        .clickable(role = Role.Tab) { onTabSelected(tab.value) }
                        .padding(vertical = Dimension.Spacing.space5),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xxs),
                ) {
                    Box(
                        modifier = Modifier
                            .size(
                                width = Dimension.Size.navigationIndicatorWidth,
                                height = Dimension.Size.navigationIndicatorHeight,
                            )
                            .background(
                                color = if (selected) tseletrackRedTint else Color.Transparent,
                                shape = AppShapes.pill,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppNavigationIcon(
                            iconRes = tab.iconRes,
                            color = if (selected) tseletrackRed else tseletrackMut,
                        )
                    }
                    Text(
                        text = stringResource(tab.labelRes),
                        color = if (selected) tseletrackInk else tseletrackMut,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchIcon() {
    Canvas(Modifier.size(15.dp)) {
        drawCircle(
            color = tseletrackMut,
            radius = 4.2.dp.toPx(),
            center = Offset(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 1.4.dp.toPx()),
        )
        drawLine(
            color = tseletrackMut,
            start = Offset(9.dp.toPx(), 9.dp.toPx()),
            end = Offset(13.dp.toPx(), 13.dp.toPx()),
            strokeWidth = 1.4.dp.toPx(),
        )
    }
}

@Composable
private fun AppNavigationIcon(
    @DrawableRes iconRes: Int,
    color: Color,
) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = color,
    )
}
