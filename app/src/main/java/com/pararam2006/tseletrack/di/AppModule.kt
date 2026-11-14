package com.pararam2006.tseletrack.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.pararam2006.tseletrack.BuildConfig
import com.pararam2006.tseletrack.data.admin.AdminRepository
import com.pararam2006.tseletrack.data.admin.NetworkAdminRepository
import com.pararam2006.tseletrack.data.auth.AuthRepository
import com.pararam2006.tseletrack.data.auth.NetworkAuthRepository
import com.pararam2006.tseletrack.data.company.CompanyRepository
import com.pararam2006.tseletrack.data.company.NetworkCompanyRepository
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.createHttpClient
import com.pararam2006.tseletrack.data.storage.TokenStore
import com.pararam2006.tseletrack.data.student.NetworkStudentRepository
import com.pararam2006.tseletrack.data.student.StudentRepository
import com.pararam2006.tseletrack.data.university.NetworkUniversityRepository
import com.pararam2006.tseletrack.data.university.UniversityRepository
import com.pararam2006.tseletrack.ui.admin.AdminViewModel
import com.pararam2006.tseletrack.ui.company.CompanyViewModel
import com.pararam2006.tseletrack.ui.login.LoginViewModel
import com.pararam2006.tseletrack.ui.registration.RegistrationViewModel
import com.pararam2006.tseletrack.ui.student.ProfileViewModel
import com.pararam2006.tseletrack.ui.student.RouteViewModel
import com.pararam2006.tseletrack.ui.student.TasksViewModel
import com.pararam2006.tseletrack.ui.student.UniversitiesViewModel
import com.pararam2006.tseletrack.ui.university.UniversityViewModel
import com.pararam2006.tseletrack.ui.session.SessionViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { androidContext().preferencesDataStoreFile("session") },
        )
    }
    single { TokenStore(get()) }
    single<HttpClient> { createHttpClient(get(), get()) }
    single { TseletrackApi(get(), get(), BuildConfig.API_BASE_URL) }

    single<AuthRepository> { NetworkAuthRepository(get(), get()) }
    single<StudentRepository> { NetworkStudentRepository(get()) }
    single<UniversityRepository> { NetworkUniversityRepository(get()) }
    single<CompanyRepository> { NetworkCompanyRepository(get()) }
    single<AdminRepository> { NetworkAdminRepository(get()) }

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::SessionViewModel)
    viewModelOf(::RouteViewModel)
    viewModelOf(::UniversitiesViewModel)
    viewModelOf(::TasksViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::UniversityViewModel)
    viewModelOf(::CompanyViewModel)
    viewModelOf(::AdminViewModel)
}
