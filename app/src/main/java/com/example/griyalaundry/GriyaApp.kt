package com.example.griyalaundry

import android.app.Application
import com.facebook.stetho.Stetho

class GriyaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }
}