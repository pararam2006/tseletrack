package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.ui.admin.AdminViewModel
import com.pararam2006.tseletrack.ui.career.CareerGuidanceViewModel
import com.pararam2006.tseletrack.ui.company.CompanyViewModel
import com.pararam2006.tseletrack.ui.events.CompanyEventsViewModel
import com.pararam2006.tseletrack.ui.events.StudentEmployerEventsViewModel
import com.pararam2006.tseletrack.ui.login.LoginViewModel
import com.pararam2006.tseletrack.ui.registration.RegistrationViewModel
import com.pararam2006.tseletrack.ui.session.SessionViewModel
import com.pararam2006.tseletrack.ui.student.AcademicPerformanceViewModel
import com.pararam2006.tseletrack.ui.student.DocumentsViewModel
import com.pararam2006.tseletrack.ui.student.ProfileViewModel
import com.pararam2006.tseletrack.ui.student.RouteViewModel
import com.pararam2006.tseletrack.ui.student.TargetOffersViewModel
import com.pararam2006.tseletrack.ui.university.UniversityViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::SessionViewModel)
    viewModelOf(::CareerGuidanceViewModel)
    viewModelOf(::RouteViewModel)
    viewModelOf(::TargetOffersViewModel)
    viewModelOf(::DocumentsViewModel)
    viewModelOf(::AcademicPerformanceViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::UniversityViewModel)
    viewModelOf(::CompanyViewModel)
    viewModelOf(::StudentEmployerEventsViewModel)
    viewModelOf(::CompanyEventsViewModel)
    viewModelOf(::AdminViewModel)
}
