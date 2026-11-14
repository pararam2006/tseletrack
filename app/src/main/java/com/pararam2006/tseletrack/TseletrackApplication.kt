package com.pararam2006.tseletrack

import android.app.Application
import com.pararam2006.tseletrack.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TseletrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TseletrackApplication)
            modules(appModule)
        }
    }
}
