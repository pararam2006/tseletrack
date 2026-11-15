package com.pararam2006.tseletrack.di

import org.koin.dsl.module

val appModule = module {
    includes(
        storageModule,
        sessionModule,
        networkModule,
        demoModule,
        careerGuidanceModule,
        repositoryModule,
        viewModelModule,
    )
}
