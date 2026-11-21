package com.bscan.app

import android.app.Application

class BscanApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_RUNNING_LOW && ::container.isInitialized) {
            container.bitmapLoader.clearMemoryCache()
        }
    }
}
