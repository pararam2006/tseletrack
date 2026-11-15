package com.pararam2006.tseletrack.ui.university

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.R
import com.pararam2006.tseletrack.domain.role.OfferDemandStatus
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric
import com.pararam2006.tseletrack.ui.common.AppCard
import com.pararam2006.tseletrack.ui.common.AppFilterChip
import com.pararam2006.tseletrack.ui.common.AppMetricCard
import com.pararam2006.tseletrack.ui.common.AppOutlinedButton
import com.pararam2006.tseletrack.ui.common.AppProgressBar
import com.pararam2006.tseletrack.ui.common.AppScreen
import com.pararam2006.tseletrack.ui.theme.AppTextStyles
import com.pararam2006.tseletrack.ui.theme.Dimension
import com.pararam2006.tseletrack.ui.theme.tseletrackInfo
import com.pararam2006.tseletrack.ui.theme.tseletrackInfoTint
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackOkTint
import com.pararam2006.tseletrack.ui.theme.tseletrackWarn
import com.pararam2006.tseletrack.ui.theme.tseletrackWarnTint

@Composable
internal fun UniversityAdmissionAnalyticsScreen(
    metrics: List<TargetOfferMetric>,
    visibleMetrics: List<TargetOfferMetric>,
    selectedFilter: OfferDemandFilter,
    onFilterSelected: (OfferDemandFilter) -> Unit,
) {
    val context = LocalContext.current
    var reportShareFailed by rememberSaveable { mutableStateOf(false) }
    val totalPlaces = metrics.sumOf(TargetOfferMetric::quotaPlaces)
    val totalApplications = metrics.sumOf(TargetOfferMetric::applicationCount)
    val riskCount = metrics.count { it.demandStatus == OfferDemandStatus.Underfilled }

    AppScreen(title = stringResource(R.string.university_analytics_title)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimension.Padding.screen),
            verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.mediumLarge),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.small)) {
                    Text(
                        text = stringResource(R.string.university_analytics_realtime),
                        color = tseletrackInk,
                        style = AppTextStyles.sectionTitle,
                    )
                    Text(
                        text = stringResource(R.string.university_analytics_description),
                        color = tseletrackMut,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            item {
                AppOutlinedButton(
                    text = stringResource(R.string.university_report_share),
                    onClick = {
                        reportShareFailed = !context.shareTargetOfferReport(metrics)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (reportShareFailed) {
                item {
                    Text(
                        text = stringResource(R.string.university_report_error),
                        color = tseletrackWarn,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.medium),
                ) {
                    AppMetricCard(
                        value = totalPlaces.toString(),
                        label = stringResource(R.string.university_analytics_places),
                        modifier = Modifier.weight(1f),
                    )
                    AppMetricCard(
                        value = totalApplications.toString(),
                        label = stringResource(R.string.university_analytics_applications),
                        valueColor = tseletrackInfo,
                        modifier = Modifier.weight(1f),
                    )
                    AppMetricCard(
                        value = riskCount.toString(),
                        label = stringResource(R.string.university_analytics_at_risk),
                        valueColor = tseletrackWarn,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimension.Spacing.small),
                ) {
                    OfferDemandFilter.entries.forEach { filter ->
                        AppFilterChip(
                            text = filter.label(),
                            isSelected = filter == selectedFilter,
                            onClick = { onFilterSelected(filter) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            if (visibleMetrics.isEmpty()) {
                item {
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.university_analytics_empty),
                            color = tseletrackMut,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            items(visibleMetrics, key = TargetOfferMetric::offerId) { metric ->
                TargetOfferMetricCard(metric)
            }
        }
    }
}

@Composable
private fun TargetOfferMetricCard(metric: TargetOfferMetric) {
    val (statusBackground, statusColor, statusLabel) = when (metric.demandStatus) {
        OfferDemandStatus.Underfilled -> Triple(
            tseletrackWarnTint,
            tseletrackWarn,
            stringResource(R.string.university_offer_underfilled),
        )

        OfferDemandStatus.Filled -> Triple(
            tseletrackOkTint,
            tseletrackOk,
            stringResource(R.string.university_offer_filled),
        )

        OfferDemandStatus.Oversubscribed -> Triple(
            tseletrackInfoTint,
            tseletrackInfo,
            stringResource(R.string.university_offer_oversubscribed),
        )
    }
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimension.Spacing.xs),
            ) {
                Text(
                    text = "${metric.directionCode} ${metric.directionName}",
                    color = tseletrackInk,
                    style = AppTextStyles.cardTitle,
                )
                Text(
                    text = metric.companyName,
                    color = tseletrackMut,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.padding(start = Dimension.Spacing.medium))
            Box(
                modifier = Modifier
                    .background(statusBackground, MaterialTheme.shapes.large)
                    .padding(
                        horizontal = Dimension.Spacing.medium,
                        vertical = Dimension.Spacing.space5,
                    ),
            ) {
                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Spacer(Modifier.height(Dimension.Spacing.large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    R.string.university_offer_places_value,
                    metric.quotaPlaces,
                ),
                color = tseletrackMut,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(
                    R.string.university_offer_applications_value,
                    metric.applicationCount,
                ),
                color = tseletrackInk,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(Dimension.Spacing.medium))
        AppProgressBar(percent = metric.fillPercent.coerceIn(0, 100))
        Spacer(Modifier.height(Dimension.Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = metric.deltaLabel(),
                color = statusColor,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = stringResource(R.string.university_offer_updated, metric.updatedAt),
                color = tseletrackMut,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun OfferDemandFilter.label(): String = stringResource(
    when (this) {
        OfferDemandFilter.All -> R.string.university_filter_all
        OfferDemandFilter.Underfilled -> R.string.university_filter_underfilled
        OfferDemandFilter.Oversubscribed -> R.string.university_filter_oversubscribed
    },
)

@Composable
private fun TargetOfferMetric.deltaLabel(): String = when {
    delta < 0 -> stringResource(R.string.university_offer_missing, -delta)
    delta > 0 -> stringResource(R.string.university_offer_over, delta)
    else -> stringResource(R.string.university_offer_exact)
}
