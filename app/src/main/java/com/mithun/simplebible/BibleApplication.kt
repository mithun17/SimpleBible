package com.mithun.simplebible

import android.app.Application
import com.facebook.stetho.Stetho

class BibleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }
}
