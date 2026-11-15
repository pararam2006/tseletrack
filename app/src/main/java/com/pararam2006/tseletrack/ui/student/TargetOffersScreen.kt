package com.pararam2006.tseletrack.ui.student

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.student.AdmissionState
import com.pararam2006.tseletrack.domain.student.AdmissionStatus
import com.pararam2006.tseletrack.domain.student.TargetOffer
import com.pararam2006.tseletrack.ui.common.AppLoadingScreen
import com.pararam2006.tseletrack.ui.common.AppScreenHeader
import com.pararam2006.tseletrack.ui.common.AppSearchField
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
import com.pararam2006.tseletrack.ui.theme.tseletrackWhite
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.exp

@Composable
internal fun TargetOffersScreen(
    state: TargetOffersUiState,
    onSearchQueryChange: (String) -> Unit,
    onRefreshCatalog: () -> Unit,
    onSaveOfferToRoute: (String) -> Unit,
    onShowCatalog: () -> Unit,
    onApplicantCodeChange: (String) -> Unit,
    onSaveApplicantCode: () -> Unit,
    onRefreshAdmissionStatus: () -> Unit,
) {
    if (state.isLoading) {
        AppLoadingScreen(title = stringResource(R.string.target_offers_title))
        return
    }

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val maxScrollSpeedPx = with(LocalDensity.current) { SearchScrollMaxSpeed.toPx() }
    var externalLinkFailed by rememberSaveable { mutableStateOf(false) }
    var scrollToSearchAfterExpansion by rememberSaveable { mutableStateOf(false) }
    val openExternalUrl: (String) -> Unit = { url ->
        externalLinkFailed = !context.openSafeExternalUrl(url)
    }
    val searchItemIndex = catalogSearchItemIndex(state, externalLinkFailed)

    LaunchedEffect(state.isCatalogExpanded, scrollToSearchAfterExpansion, searchItemIndex) {
        if (state.isCatalogExpanded && scrollToSearchAfterExpansion) {
            listState.animateScrollToItemSmoothly(searchItemIndex, maxScrollSpeedPx)
            scrollToSearchAfterExpansion = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tseletrackBg),
    ) {
        AppScreenHeader(title = stringResource(R.string.target_offers_title))
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.target_offers_intro),
                    color = tseletrackMut,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )
            }
            state.catalogWarning?.let { warning ->
                item { NoticeCard(warning, tseletrackWarnTint, tseletrackWarn) }
            }
            state.actionMessage?.let { message ->
                item { NoticeCard(message.asText(), tseletrackInfoTint, tseletrackInfo) }
            }
            if (externalLinkFailed) {
                item {
                    NoticeCard(
                        stringResource(R.string.target_offer_open_link_error),
                        tseletrackWarnTint,
                        tseletrackWarn,
                    )
                }
            }
            state.selectedOffer?.let { selectedOffer ->
                item {
                    Text(
                        text = stringResource(R.string.target_offer_selected_title),
                        color = tseletrackInk,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                item {
                    TargetOfferCard(
                        offer = selectedOffer,
                        isSelected = true,
                        isSelecting = false,
                        selectionEnabled = false,
                        onSaveToRoute = {},
                        onApply = openExternalUrl,
                        onOpenSource = openExternalUrl,
                        onChangeSelection = {
                            scrollToSearchAfterExpansion = true
                            onShowCatalog()
                        },
                    )
                }
            }
            item {
                AdmissionTrackingCard(
                    applicantCode = state.applicantCode,
                    status = state.admissionStatus,
                    canSave = state.canSaveApplicantCode,
                    isSaving = state.isSavingCode,
                    isRefreshing = state.isRefreshingStatus,
                    onApplicantCodeChange = onApplicantCodeChange,
                    onSaveApplicantCode = onSaveApplicantCode,
                    onRefreshAdmissionStatus = onRefreshAdmissionStatus,
                )
            }
            if (state.isCatalogExpanded) {
                item {
                    Text(
                        text = stringResource(
                            if (state.selectedOffer == null) {
                                R.string.target_offers_catalog_title
                            } else {
                                R.string.target_offers_other_title
                            },
                        ),
                        color = tseletrackInk,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                item {
                    AppSearchField(
                        value = state.searchQuery,
                        placeholder = stringResource(R.string.target_offers_search),
                        onValueChange = onSearchQueryChange,
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.target_offers_count,
                                state.visibleOffers.size,
                            ),
                            color = tseletrackMut,
                            fontSize = 12.sp,
                        )
                        SmallAction(
                            text = stringResource(
                                if (state.isRefreshingCatalog) {
                                    R.string.target_offers_refreshing
                                } else {
                                    R.string.target_offers_refresh
                                },
                            ),
                            enabled = !state.isRefreshingCatalog,
                            onClick = onRefreshCatalog,
                        )
                    }
                }
                if (state.visibleOffers.isEmpty()) {
                    item {
                        NoticeCard(
                            stringResource(R.string.target_offers_empty),
                            tseletrackWhite,
                            tseletrackMut,
                        )
                    }
                }
                items(state.visibleOffers, key = TargetOffer::id) { offer ->
                    TargetOfferCard(
                        offer = offer,
                        isSelected = false,
                        isSelecting = state.selectingOfferId == offer.id,
                        selectionEnabled = state.selectingOfferId == null,
                        onSaveToRoute = { onSaveOfferToRoute(offer.id) },
                        onApply = openExternalUrl,
                        onOpenSource = openExternalUrl,
                        onChangeSelection = {},
                    )
                }
            }
        }
    }
}

private suspend fun LazyListState.animateScrollToItemSmoothly(
    index: Int,
    maxSpeedPxPerSecond: Float,
) {
    scroll {
        val layoutScope = LazyLayoutScrollScope(this@animateScrollToItemSmoothly, this)
        var previousFrameNanos = withFrameNanos { it }

        while (true) {
            val distance = layoutScope.calculateDistanceTo(index).toFloat()
            if (abs(distance) < 1f) return@scroll

            val frameNanos = withFrameNanos { it }
            val frameSeconds = ((frameNanos - previousFrameNanos) / NanosPerSecond)
                .coerceIn(0f, MaxFrameDurationSeconds)
            previousFrameNanos = frameNanos

            val maxStep = maxSpeedPxPerSecond * frameSeconds
            val easingStep = abs(distance) *
                (1f - exp(-frameSeconds / SearchScrollEasingTimeSeconds))
            val direction = if (distance > 0f) 1f else -1f
            val step = minOf(abs(distance), maxStep, easingStep) * direction

            val consumed = layoutScope.scrollBy(step)
            if (abs(consumed) < 0.5f) return@scroll
        }
    }
}

private fun catalogSearchItemIndex(
    state: TargetOffersUiState,
    externalLinkFailed: Boolean,
): Int {
    var index = 1 // Intro.
    if (state.catalogWarning != null) index++
    if (state.actionMessage != null) index++
    if (externalLinkFailed) index++
    if (state.selectedOffer != null) index += 2 // Section title and selected offer.
    index++ // Admission tracking.
    index++ // Catalog title.
    return index
}

private val SearchScrollMaxSpeed = 840.dp
private const val SearchScrollEasingTimeSeconds = 0.09f
private const val MaxFrameDurationSeconds = 0.1f
private const val NanosPerSecond = 1_000_000_000f

@Composable
private fun AdmissionTrackingCard(
    applicantCode: String,
    status: AdmissionStatus?,
    canSave: Boolean,
    isSaving: Boolean,
    isRefreshing: Boolean,
    onApplicantCodeChange: (String) -> Unit,
    onSaveApplicantCode: () -> Unit,
    onRefreshAdmissionStatus: () -> Unit,
) {
    val applicantCodeDescription = stringResource(R.string.admission_code_hint)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = tseletrackWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, tseletrackLine),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.admission_status_title),
                color = tseletrackInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.admission_status_description),
                color = tseletrackMut,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
            status?.let { AdmissionStatusContent(it) }
            BasicTextField(
                value = applicantCode,
                onValueChange = onApplicantCodeChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .background(tseletrackBg, RoundedCornerShape(10.dp))
                    .border(1.dp, tseletrackLine, RoundedCornerShape(10.dp))
                    .semantics { contentDescription = applicantCodeDescription },
                textStyle = TextStyle(color = tseletrackInk, fontSize = 14.sp),
                cursorBrush = SolidColor(tseletrackRed),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { input ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (applicantCode.isEmpty()) {
                            Text(
                                text = stringResource(R.string.admission_code_hint),
                                color = tseletrackMut,
                                fontSize = 13.sp,
                            )
                        }
                        input()
                    }
                },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallAction(
                    text = stringResource(
                        if (isSaving) R.string.admission_saving else R.string.admission_save_code,
                    ),
                    enabled = canSave,
                    isPrimary = true,
                    onClick = onSaveApplicantCode,
                )
                if (status != null) {
                    SmallAction(
                        text = stringResource(
                            if (isRefreshing) {
                                R.string.admission_checking
                            } else {
                                R.string.admission_check_status
                            },
                        ),
                        enabled = !isRefreshing,
                        onClick = onRefreshAdmissionStatus,
                    )
                }
            }
            Text(
                text = stringResource(R.string.admission_code_privacy),
                color = tseletrackMut,
                fontSize = 10.sp,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun AdmissionStatusContent(status: AdmissionStatus) {
    val (background, foreground) = when (status.state) {
        AdmissionState.Unknown -> tseletrackWarnTint to tseletrackWarn
        AdmissionState.ApplicationFound,
        AdmissionState.Recommended -> tseletrackInfoTint to tseletrackInfo
        AdmissionState.Enrolled -> tseletrackOkTint to tseletrackOk
    }
    val label = when (status.state) {
        AdmissionState.Unknown -> R.string.admission_state_unknown
        AdmissionState.ApplicationFound -> R.string.admission_state_found
        AdmissionState.Recommended -> R.string.admission_state_recommended
        AdmissionState.Enrolled -> R.string.admission_state_enrolled
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = status.applicantCodeMasked,
            color = tseletrackInk,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Box(
            modifier = Modifier
                .background(background, RoundedCornerShape(12.dp))
                .padding(horizontal = 9.dp, vertical = 5.dp),
        ) {
            Text(
                text = stringResource(label),
                color = foreground,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
    val direction = listOfNotNull(status.directionCode, status.directionName)
        .joinToString(" ")
    if (direction.isNotBlank()) {
        DetailLine(stringResource(R.string.admission_direction), direction)
    }
    status.totalScore?.let {
        DetailLine(stringResource(R.string.admission_score), it.toString())
    }
    status.sponsorName?.let {
        DetailLine(stringResource(R.string.admission_sponsor), it)
    }
    status.warning?.let {
        Text(text = it, color = tseletrackWarn, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun TargetOfferCard(
    offer: TargetOffer,
    isSelected: Boolean,
    isSelecting: Boolean,
    selectionEnabled: Boolean,
    onSaveToRoute: () -> Unit,
    onApply: (String) -> Unit,
    onOpenSource: (String) -> Unit,
    onChangeSelection: () -> Unit,
) {
    val externalLinks = offer.externalLinks()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = tseletrackWhite,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) tseletrackRed else tseletrackLine,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                val offerTitle = listOfNotNull(offer.directionCode, offer.directionName)
                    .joinToString(" ")
                Text(
                    text = if (offerTitle.isBlank()) {
                        stringResource(R.string.target_offer_without_direction)
                    } else {
                        offerTitle
                    },
                    modifier = Modifier.weight(1f),
                    color = tseletrackInk,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (isSelected) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.target_offer_selected),
                        modifier = Modifier
                            .background(tseletrackRedTint, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = tseletrackRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            offer.profile?.let {
                Text(text = it, color = tseletrackMut, fontSize = 12.sp, lineHeight = 17.sp)
            }
            offer.proposerName?.let {
                DetailLine(stringResource(R.string.target_offer_customer), it)
            }
            offer.workplaceAddress?.let {
                DetailLine(stringResource(R.string.target_offer_workplace), it)
            }
            val education = listOfNotNull(
                offer.programLevelName,
                offer.educationFormName,
            ).joinToString(" · ")
            if (education.isNotBlank()) {
                DetailLine(stringResource(R.string.target_offer_education), education)
            }
            offer.applicationDeadline?.let {
                DetailLine(stringResource(R.string.target_offer_deadline), it.toDisplayDate())
            }
            Text(
                text = stringResource(R.string.target_offer_source),
                color = tseletrackMut,
                fontSize = 10.sp,
            )
            SmallAction(
                text = stringResource(R.string.target_offer_apply),
                modifier = Modifier.fillMaxWidth(),
                enabled = externalLinks.applicationUrl != null,
                isPrimary = true,
                onClick = { externalLinks.applicationUrl?.let(onApply) },
            )
            if (externalLinks.shouldShowSourceLink) {
                SmallAction(
                    text = stringResource(R.string.target_offer_open_source),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = externalLinks.sourceUrl != null,
                    onClick = { externalLinks.sourceUrl?.let(onOpenSource) },
                )
            }
            SmallAction(
                text = stringResource(
                    when {
                        isSelected -> R.string.target_offer_choose_another
                        isSelecting -> R.string.target_offer_selecting
                        else -> R.string.target_offer_save_to_route
                    },
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = isSelected || selectionEnabled,
                onClick = if (isSelected) onChangeSelection else onSaveToRoute,
            )
        }
    }
}

private fun String.toDisplayDate(): String = runCatching {
    LocalDateTime.parse(this).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}.getOrElse {
    take(10).split('-').let { parts ->
        if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else this
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        color = tseletrackMut,
        fontSize = 11.sp,
        lineHeight = 15.sp,
    )
}

@Composable
private fun NoticeCard(text: String, background: Color, foreground: Color) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(12.dp))
            .border(1.dp, tseletrackLine, RoundedCornerShape(12.dp))
            .padding(12.dp),
        color = foreground,
        fontSize = 12.sp,
        lineHeight = 17.sp,
    )
}

@Composable
private fun SmallAction(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
) {
    val background = when {
        !enabled -> tseletrackLine
        isPrimary -> tseletrackRed
        else -> Color.Transparent
    }
    val foreground = when {
        !enabled -> tseletrackMut
        isPrimary -> tseletrackWhite
        else -> tseletrackInk
    }
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(background, RoundedCornerShape(9.dp))
            .border(1.dp, if (isPrimary && enabled) tseletrackRed else tseletrackLine, RoundedCornerShape(9.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun TargetOffersMessage.asText(): String = when (this) {
    TargetOffersMessage.CatalogUpdated -> stringResource(R.string.target_offers_catalog_updated)
    TargetOffersMessage.OfferSavedToRoute -> stringResource(R.string.target_offer_saved_to_route)
    TargetOffersMessage.InvalidApplicantCode -> stringResource(R.string.admission_code_invalid)
    TargetOffersMessage.ApplicantCodeSaved -> stringResource(R.string.admission_code_saved)
    TargetOffersMessage.AdmissionStatusUpdated -> stringResource(R.string.admission_status_updated)
    is TargetOffersMessage.Error -> value
}

private fun Context.openSafeExternalUrl(rawUrl: String): Boolean {
    val safeUrl = rawUrl.toSafeExternalUrlOrNull() ?: return false
    val uri = safeUrl.toUri()

    val customTabOpened = runCatching {
        CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(this, uri)
    }.isSuccess
    if (customTabOpened) return true

    return runCatching {
        startActivity(
            Intent(Intent.ACTION_VIEW, uri)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }.isSuccess
}
