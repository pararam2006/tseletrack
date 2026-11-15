package com.pararam2006.tseletrack.domain.student

data class AcademicResult(
    val semester: Int,
    val discipline: String,
    val points: String?,
    val grade: String?,
    val teacher: String?,
    val retakeInfo: String?,
    val canBeRecredited: Boolean,
)

data class AcademicPerformance(
    val recordBookNumberMasked: String,
    val groupTitle: String,
    val results: List<AcademicResult>,
    val fetchedAt: String?,
    val isStale: Boolean,
    val warning: String?,
    val sourceUrl: String,
    val isDemo: Boolean = false,
)
