package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.data.network.ApiErrorKind
import com.pararam2006.tseletrack.data.network.ApiException
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.dto.toDomain
import com.pararam2006.tseletrack.domain.student.AcademicPerformance

class NetworkAcademicPerformanceRepository(
    private val api: TseletrackApi,
) : AcademicPerformanceRepository {
    override suspend fun load(): AcademicPerformance? = try {
        api.academicPerformance().toDomain()
    } catch (error: ApiException) {
        if (error.kind == ApiErrorKind.NotFound) null else throw error
    }

    override suspend fun connect(
        recordBookNumber: String,
        groupTitle: String,
    ): AcademicPerformance = api.connectAcademicPerformance(
        recordBookNumber = recordBookNumber,
        groupTitle = groupTitle,
    ).toDomain()

    override suspend fun refresh(): AcademicPerformance =
        api.refreshAcademicPerformance().toDomain()
}
