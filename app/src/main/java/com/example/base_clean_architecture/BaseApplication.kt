package com.example.base_clean_architecture

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            println("Run Base App")
            Timber.plant(Timber.DebugTree())
        }
    }
}