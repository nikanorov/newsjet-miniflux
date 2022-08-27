package com.nikanorov.newsjetminiflux

import android.app.Application
import com.nikanorov.newsjetminiflux.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewsjetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@NewsjetApplication)
            modules(appModule)
        }
    }
}
