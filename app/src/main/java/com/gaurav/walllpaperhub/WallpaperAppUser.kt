package com.gaurav.walllpaperhub

import android.app.Application
import android.os.SystemClock

class WallpaperAppUser: Application() {
    override fun onCreate() {
        super.onCreate()
        SystemClock.sleep(2000)
    }
}