package com.pararam2006.tseletrack.di

import com.pararam2006.tseletrack.data.session.SessionIdentityStore
import org.koin.dsl.module

val sessionModule = module {
    single { SessionIdentityStore() }
}
