package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.domain.student.AcademicPerformance

interface AcademicPerformanceRepository {
    suspend fun load(): AcademicPerformance?
    suspend fun connect(recordBookNumber: String, groupTitle: String): AcademicPerformance
    suspend fun refresh(): AcademicPerformance
}
