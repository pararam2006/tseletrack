package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import com.pararam2006.tseletrack.domain.student.AcademicPerformance
import com.pararam2006.tseletrack.domain.student.AcademicResult

class StubAcademicPerformanceRepository(
    private val store: DemoPlatformStore = DemoPlatformStore(),
) : AcademicPerformanceRepository {
    override suspend fun load(): AcademicPerformance? = store.academicPerformance

    override suspend fun connect(
        recordBookNumber: String,
        groupTitle: String,
    ): AcademicPerformance = demoPerformance.copy(
        recordBookNumberMasked = recordBookNumber.maskRecordBookNumber(),
        groupTitle = groupTitle.trim(),
    ).also { store.academicPerformance = it }

    override suspend fun refresh(): AcademicPerformance =
        checkNotNull(store.academicPerformance) { "Сначала подключите успеваемость" }

    private companion object {
        val demoPerformance = AcademicPerformance(
            recordBookNumberMasked = "22•••47",
            groupTitle = "ИВТ-221",
            results = listOf(
                AcademicResult(1, "Математический анализ", "86", "Отлично", "Иванова Е. В.", null, false),
                AcademicResult(1, "Информатика", "91", "Отлично", "Петров А. С.", null, false),
                AcademicResult(1, "Иностранный язык", "74", "Хорошо", "Соколова Н. И.", null, false),
                AcademicResult(1, "Физическая культура", null, "Зачтено", null, null, false),
                AcademicResult(2, "Физика", "78", "Хорошо", "Кузнецов М. П.", null, false),
                AcademicResult(2, "Инженерная графика", "68", "Удовлетворительно", "Орлов Д. А.", null, false),
                AcademicResult(2, "История России", "82", "Хорошо", "Волкова Т. В.", null, true),
            ),
            fetchedAt = "Сегодня, 09:30",
            isStale = false,
            warning = null,
            sourceUrl = "https://tulsu.ru/progress/",
            isDemo = true,
        )
    }
}

internal fun String.maskRecordBookNumber(): String {
    val normalized = trim()
    if (normalized.length <= 4) return "•".repeat(normalized.length)
    return normalized.take(2) + "•".repeat(normalized.length - 4) + normalized.takeLast(2)
}
