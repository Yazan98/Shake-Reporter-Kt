package com.yazantarifi.reporter

import android.app.Application
import com.yazantarifi.android.reporter.ShakeReporter

class ApplicationInstance: Application() {

    override fun onCreate() {
        super.onCreate()
        ShakeReporter.init(this)
    }

}