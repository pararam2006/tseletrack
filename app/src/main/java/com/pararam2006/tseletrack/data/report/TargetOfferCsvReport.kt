package com.pararam2006.tseletrack.data.report

import com.pararam2006.tseletrack.domain.role.OfferDemandStatus
import com.pararam2006.tseletrack.domain.role.TargetOfferMetric

object TargetOfferCsvReport {
    fun create(metrics: List<TargetOfferMetric>): String = buildString {
        append('\uFEFF')
        appendLine(
            listOf(
                "ID предложения",
                "Код направления",
                "Направление",
                "Предприятие",
                "Целевых мест",
                "Подано заявлений",
                "Отклонение",
                "Статус",
                "Обновлено",
            ).toCsvRow(),
        )
        metrics.forEach { metric ->
            appendLine(
                listOf(
                    metric.offerId,
                    metric.directionCode,
                    metric.directionName,
                    metric.companyName,
                    metric.quotaPlaces.toString(),
                    metric.applicationCount.toString(),
                    metric.delta.toString(),
                    metric.demandStatus.reportTitle,
                    metric.updatedAt,
                ).toCsvRow(),
            )
        }
    }

    private fun List<String>.toCsvRow(): String = joinToString(";") { value ->
        "\"${value.replace("\"", "\"\"")}\""
    }

    private val OfferDemandStatus.reportTitle: String
        get() = when (this) {
            OfferDemandStatus.Underfilled -> "Недобор"
            OfferDemandStatus.Filled -> "План выполнен"
            OfferDemandStatus.Oversubscribed -> "Переподписка"
        }
}
