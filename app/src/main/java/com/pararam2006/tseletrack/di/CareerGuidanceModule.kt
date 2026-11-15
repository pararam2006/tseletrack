package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.data.career.CareerGuidanceStore
import com.pararam2006.tseletrack.domain.career.CareerGuidanceEngine
import org.koin.dsl.module

val careerGuidanceModule = module {
    single { CareerGuidanceStore(get(), get()) }
    single { CareerGuidanceEngine() }
}
