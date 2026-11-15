package com.pararam2006.tseletrack.data.student

import com.pararam2006.tseletrack.core.Role
import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.domain.student.AcademicPerformance

class SwitchingAcademicPerformanceRepository(
    private val networkRepository: NetworkAcademicPerformanceRepository,
    private val demoRepository: StubAcademicPerformanceRepository,
    private val demoModeStore: DemoModeStore,
) : AcademicPerformanceRepository {
    private val activeRepository: AcademicPerformanceRepository
        get() = if (demoModeStore.role == Role.Student) demoRepository else networkRepository

    override suspend fun load(): AcademicPerformance? = activeRepository.load()

    override suspend fun connect(
        recordBookNumber: String,
        groupTitle: String,
    ): AcademicPerformance = activeRepository.connect(recordBookNumber, groupTitle)

    override suspend fun refresh(): AcademicPerformance = activeRepository.refresh()
}
