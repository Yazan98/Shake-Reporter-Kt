package com.yazantarifi.android.reporter

interface ShakeReporterCallback {

    fun onExceptionTriggered(throwable: Throwable)

}