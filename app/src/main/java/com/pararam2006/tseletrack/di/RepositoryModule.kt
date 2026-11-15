package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.data.admin.AdminRepository
import com.pararam2006.tseletrack.data.admin.NetworkAdminRepository
import com.pararam2006.tseletrack.data.auth.AuthRepository
import com.pararam2006.tseletrack.data.auth.NetworkAuthRepository
import com.pararam2006.tseletrack.data.company.CompanyRepository
import com.pararam2006.tseletrack.data.company.NetworkCompanyRepository
import com.pararam2006.tseletrack.data.events.EmployerEventsRepository
import com.pararam2006.tseletrack.data.events.LocalEmployerEventsRepository
import com.pararam2006.tseletrack.data.student.AcademicPerformanceRepository
import com.pararam2006.tseletrack.data.student.NetworkAcademicPerformanceRepository
import com.pararam2006.tseletrack.data.student.NetworkStudentRepository
import com.pararam2006.tseletrack.data.student.StubAcademicPerformanceRepository
import com.pararam2006.tseletrack.data.student.StubStudentRepository
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.student.SwitchingAcademicPerformanceRepository
import com.pararam2006.tseletrack.data.student.SwitchingStudentRepository
import com.pararam2006.tseletrack.data.university.NetworkUniversityRepository
import com.pararam2006.tseletrack.data.university.StubUniversityRepository
import com.pararam2006.tseletrack.data.university.SwitchingUniversityRepository
import com.pararam2006.tseletrack.data.university.UniversityRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { NetworkAuthRepository(get(), get(), get(), get(), get()) }
    single { NetworkStudentRepository(get()) }
    single { StubStudentRepository(get()) }
    single<StudentRepository> { SwitchingStudentRepository(get(), get(), get()) }
    single { NetworkAcademicPerformanceRepository(get()) }
    single { StubAcademicPerformanceRepository(get(), get()) }
    single<AcademicPerformanceRepository> {
        SwitchingAcademicPerformanceRepository(get(), get(), get())
    }

    single { NetworkUniversityRepository(get()) }
    single { StubUniversityRepository(get()) }
    single<UniversityRepository> { SwitchingUniversityRepository(get(), get(), get()) }
    single<CompanyRepository> { NetworkCompanyRepository(get()) }
    single<EmployerEventsRepository> { LocalEmployerEventsRepository(get(), get()) }
    single<AdminRepository> { NetworkAdminRepository(get()) }
}
