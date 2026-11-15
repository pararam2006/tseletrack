package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.BuildConfig
import com.pararam2006.tseletrack.data.network.TseletrackApi
import com.pararam2006.tseletrack.data.network.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single<HttpClient> { createHttpClient(get(), get()) }
    single { TseletrackApi(get(), get(), BuildConfig.API_BASE_URL) }
}
