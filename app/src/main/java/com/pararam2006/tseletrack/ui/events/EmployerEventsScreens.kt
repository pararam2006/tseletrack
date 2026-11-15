package com.pararam2006.tseletrack.ui.events

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.events.EmployerEvent
import com.pararam2006.tseletrack.domain.events.EmployerEventSummary
import com.pararam2006.tseletrack.domain.events.ManagedEmployerEvent
import com.pararam2006.tseletrack.ui.common.AppBackScreenHeader
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppPrimaryButton
import com.pararam2006.tseletrack.ui.common.AppScreen
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.ui.theme.tseletrackBg
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackRed
import com.pararam2006.tseletrack.ui.theme.tseletrackRedTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint
import java.time.format.DateTimeFormatter
import java.util.Locale

private val EventDateFormatter = DateTimeFormatter.ofPattern(
    "d MMMM, EEEE · HH:mm",
    Locale.forLanguageTag("ru"),
)
private const val ParticipantPreviewLimit = 4

@Composable
internal fun StudentEmployerEventsScreen(
    state: StudentEmployerEventsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleRegistration: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppBackScreenHeader(
            title = stringResource(R.string.employer_events_title),
            onBack = onBack,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        ) {
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = state.organizationName,
                        color = tseletrackInk,
                        style = AppTextStyles.cardTitle,
                    )
                    Spacer(Modifier.height(Dimension.Spacing.small))
                    Text(
                        text = stringResource(R.string.employer_events_student_intro),
                        color = tseletrackMut,
                        style = AppTextStyles.bodyCompact,
                    )
                }
            }
            state.message?.let { message ->
                item { EventNotice(text = message, isError = false) }
            }
            state.errorMessage?.let { message ->
                item { EventNotice(text = message, isError = true) }
            }
            when {
                state.isLoading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimension.Spacing.huge),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = tseletrackRed)
                    }
                }

                state.events.isEmpty() && state.errorMessage == null -> item {
                    EventEmptyCard(stringResource(R.string.employer_events_empty))
                }

                state.events.isEmpty() -> item {
                    AppPrimaryButton(
                        text = stringResource(R.string.common_retry),
                        onClick = onRetry,
                    )
                }

                else -> items(state.events, key = { it.event.id }) { summary ->
                    StudentEventCard(
                        summary = summary,
                        isUpdating = state.updatingEventId == summary.event.id,
                        onToggleRegistration = { onToggleRegistration(summary.event.id) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun CompanyEmployerEventsScreen(
    state: CompanyEventsUiState,
) {
    AppScreen(title = stringResource(R.string.employer_events_company_title)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.large),
        ) {
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = state.organizationName,
                        color = tseletrackInk,
                        style = AppTextStyles.cardTitle,
                    )
                    Spacer(Modifier.height(Dimension.Spacing.small))
                    Text(
                        text = stringResource(
                            R.string.employer_events_company_summary,
                            state.events.size,
                            state.totalRegistrations,
                        ),
                        color = tseletrackMut,
                        style = AppTextStyles.bodyCompact,
                    )
                }
            }
            when {
                state.isLoading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimension.Spacing.huge),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = tseletrackRed)
                    }
                }

                state.events.isEmpty() -> item {
                    EventEmptyCard(stringResource(R.string.employer_events_company_empty))
                }

                else -> items(state.events, key = { it.event.id }) { managedEvent ->
                    CompanyEventCard(managedEvent)
                }
            }
        }
    }
}

@Composable
private fun StudentEventCard(
    summary: EmployerEventSummary,
    isUpdating: Boolean,
    onToggleRegistration: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        EventDetails(event = summary.event)
        Spacer(Modifier.height(Dimension.Spacing.large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    R.string.employer_event_registration_count,
                    summary.registeredCount,
                    summary.event.capacity,
                ),
                color = tseletrackMut,
                style = AppTextStyles.caption,
            )
            Text(
                text = if (summary.isRegistered) {
                    stringResource(R.string.employer_event_registered)
                } else {
                    stringResource(R.string.employer_event_places_left, summary.availablePlaces)
                },
                modifier = Modifier
                    .background(
                        if (summary.isRegistered) tseletrackOkTint else tseletrackInfoTint,
                        MaterialTheme.shapes.small,
                    )
                    .padding(
                        horizontal = Dimension.Spacing.medium,
                        vertical = Dimension.Spacing.xs,
                    ),
                color = if (summary.isRegistered) tseletrackOk else tseletrackInfo,
                style = AppTextStyles.status,
            )
        }
        Spacer(Modifier.height(Dimension.Spacing.large))
        AppPrimaryButton(
            text = when {
                isUpdating -> stringResource(R.string.employer_event_updating)
                summary.isRegistered -> stringResource(R.string.employer_event_cancel)
                summary.canRegister -> stringResource(R.string.employer_event_register)
                else -> stringResource(R.string.employer_event_full)
            },
            enabled = !isUpdating && summary.canRegister,
            onClick = onToggleRegistration,
        )
    }
}

@Composable
private fun CompanyEventCard(managedEvent: ManagedEmployerEvent) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        EventDetails(event = managedEvent.event)
        Spacer(Modifier.height(Dimension.Spacing.large))
        Text(
            text = stringResource(
                R.string.employer_event_registration_count,
                managedEvent.registeredCount,
                managedEvent.event.capacity,
            ),
            color = tseletrackRed,
            style = AppTextStyles.bodyCompactStrong,
        )
        Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
        Text(
            text = stringResource(R.string.employer_event_latest_participants),
            color = tseletrackInk,
            style = AppTextStyles.bodyCompactStrong,
        )
        Spacer(Modifier.height(Dimension.Spacing.small))
        if (managedEvent.participants.isEmpty()) {
            Text(
                text = stringResource(R.string.employer_event_no_participants),
                color = tseletrackMut,
                style = AppTextStyles.caption,
            )
        } else {
            managedEvent.participants.takeLast(ParticipantPreviewLimit).forEach { participant ->
                Text(
                    text = "• ${participant.displayName}",
                    color = tseletrackMut,
                    style = AppTextStyles.bodyCompact,
                )
            }
            val hiddenCount = managedEvent.registeredCount -
                managedEvent.participants.takeLast(ParticipantPreviewLimit).size
            if (hiddenCount > 0) {
                Text(
                    text = stringResource(R.string.employer_event_more_participants, hiddenCount),
                    color = tseletrackMut,
                    style = AppTextStyles.caption,
                )
            }
        }
    }
}

@Composable
private fun EventDetails(event: EmployerEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.small),
    ) {
        EventTag(event.type.title, tseletrackRedTint, tseletrackRed)
        EventTag(event.format.title, tseletrackInfoTint, tseletrackInfo)
    }
    Spacer(Modifier.height(Dimension.Spacing.mediumLarge))
    Text(
        text = event.title,
        color = tseletrackInk,
        style = AppTextStyles.cardTitle,
    )
    Spacer(Modifier.height(Dimension.Spacing.small))
    Text(
        text = event.startsAt.format(EventDateFormatter),
        color = tseletrackRed,
        style = AppTextStyles.bodyCompactStrong,
    )
    Text(
        text = event.location,
        color = tseletrackMut,
        style = AppTextStyles.caption,
    )
    Spacer(Modifier.height(Dimension.Spacing.medium))
    Text(
        text = event.description,
        color = tseletrackInk,
        style = AppTextStyles.bodyCompact,
    )
}

@Composable
private fun EventTag(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    foreground: androidx.compose.ui.graphics.Color,
) {
    Text(
        text = text,
        modifier = Modifier
            .background(background, MaterialTheme.shapes.small)
            .padding(horizontal = Dimension.Spacing.medium, vertical = Dimension.Spacing.xs),
        color = foreground,
        style = AppTextStyles.status,
    )
}

@Composable
private fun EventNotice(text: String, isError: Boolean) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isError) tseletrackWarnTint else tseletrackOkTint,
                MaterialTheme.shapes.large,
            )
            .padding(Dimension.Spacing.large),
        color = if (isError) tseletrackWarn else tseletrackOk,
        style = AppTextStyles.bodyCompact,
    )
}

@Composable
private fun EventEmptyCard(message: String) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = message,
            color = tseletrackMut,
            style = AppTextStyles.bodyCompact,
        )
    }
}
