package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.data.demo.DemoModeStore
import com.pararam2006.tseletrack.data.demo.DemoPlatformStore
import org.koin.dsl.module

val demoModule = module {
    single { DemoModeStore() }
    single { DemoPlatformStore() }
}
