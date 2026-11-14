package com.pararam2006.tseletrack.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pararam2006.tseletrack.domain.role.AnalyticsDashboard
import com.pararam2006.tseletrack.ui.theme.tseletrackInk
import com.pararam2006.tseletrack.ui.theme.tseletrackLine
import com.pararam2006.tseletrack.ui.theme.tseletrackMut
import com.pararam2006.tseletrack.ui.theme.tseletrackOk
import com.pararam2006.tseletrack.ui.theme.tseletrackRed

@Composable
internal fun EmploymentAnalyticsScreen(
    analytics: AnalyticsDashboard,
    modifier: Modifier = Modifier,
) {
    AppScreen(
        title = "Аналитика",
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppMetricCard(
                    value = analytics.candidates.toString(),
                    label = "кандидатов",
                    modifier = Modifier.weight(1f),
                )
                AppMetricCard(
                    value = "${analytics.employedPercent}%",
                    label = "трудоустроены",
                    valueColor = tseletrackOk,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppMetricCard(
                    value = analytics.inEducation.toString(),
                    label = "в обучении",
                    modifier = Modifier.weight(1f),
                )
                AppMetricCard(
                    value = analytics.waitingDecision.toString(),
                    label = "ждут решения",
                    valueColor = com.pararam2006.tseletrack.ui.theme.tseletrackWarn,
                    modifier = Modifier.weight(1f),
                )
            }
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Box(
                        modifier = Modifier.size(112.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmploymentDonut(percent = analytics.employedPercent)
                        Text(
                            text = "${analytics.employedPercent}%",
                            color = tseletrackInk,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column {
                        Text(
                            text = "Трудоустроены",
                            color = tseletrackInk,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Удержаны",
                            color = tseletrackMut,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = "Кандидаты по вузам",
                    color = tseletrackInk,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    val max = analytics.universityValues.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
                    analytics.universityValues.forEach { (name, value) ->
                        UniversityBar(
                            name = name,
                            fraction = value / max.toFloat(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmploymentDonut(percent: Int) {
    Canvas(Modifier.fillMaxSize()) {
        val strokeWidth = 14.dp.toPx()
        drawCircle(
            color = tseletrackLine,
            radius = (size.minDimension - strokeWidth) / 2,
            style = Stroke(strokeWidth),
        )
        val segmentSweep = 36f
        repeat(5) { index ->
            val start = -90f + index * 72f
            drawArc(
                color = tseletrackOk,
                startAngle = start,
                sweepAngle = segmentSweep,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Butt),
            )
        }
    }
}

@Composable
private fun UniversityBar(
    name: String,
    fraction: Float,
) {
    Column(
        modifier = Modifier
            .height(130.dp)
            .width(46.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(
            modifier = Modifier
                .width(34.dp)
                .height((104f * fraction.coerceIn(0.15f, 1f)).dp)
                .background(tseletrackRed, RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp)),
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = name,
            color = tseletrackMut,
            fontSize = 9.sp,
            maxLines = 1,
        )
    }
}
