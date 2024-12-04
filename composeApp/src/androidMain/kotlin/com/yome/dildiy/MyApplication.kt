package com.yome.dildiy

import android.app.Application
import com.yome.dildiy.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule())
        }
    }
}