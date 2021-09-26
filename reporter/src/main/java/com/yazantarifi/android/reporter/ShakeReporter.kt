package com.yazantarifi.android.reporter

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object ShakeReporter {

    const val CRASH_FILE_PATH = "crashes"
    private const val FILES_ROOT_PATH = "ShakeReporter"
    private const val LOGGING_PREFIX = "[ShakeReporter]:"

    /**
     * Init The Library With Storage Path and Generated File in Private Storage for Application
     * Used To Save All Exceptions and Network Calls To See The History Via Notification
     * Here The Default Notification Channel Will Be Created
     */
    @JvmStatic
    fun init(context: Context) {
        ReporterNotifications.createNotificationChannel(context)
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            onCrashTriggered(thread, throwable, context)
        }
    }

    /**
     * If You Want to Submit Crash in Custom Callback Use This Method
     * You want To Add Extra Params Like Printing Non Fatal Crashes
     * Print it on Callback then Call this Method to report the Crash Internally
     */
    @JvmStatic
    fun onCrashTriggered(thread: Thread, throwable: Throwable, context: Context) {
        val reporterFilesPath = File(context.filesDir, FILES_ROOT_PATH)
        if (!reporterFilesPath.exists()) {
            reporterFilesPath.mkdir()
            printLogs("Create Root Crashes Path : ${reporterFilesPath.absoluteFile}")
        }

        createExceptionReport(thread, throwable, reporterFilesPath)
        ReporterNotifications.showNotification(
            getNotificationTitle(),
            getNotificationMessage(thread, throwable),
            context
        )
    }

    /**
     * Use this Method when you want to Submit Any Callback
     * to do Custom Work on The Exceptions Then Will Call the Default
     * Handler Automatic
     */
    @JvmStatic
    fun init(context: Context, callback: ShakeReporterCallback) {
        init(context)
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            callback.onExceptionTriggered(throwable)
            onCrashTriggered(thread, throwable, context)
        }
    }

    /**
     * Each Crash Will be Saved in Application Private Storage to Access It From Screens
     * This Method Don't need Storage Permission
     * Each Exception Record is Saved in One File with Name : CRASH_FILE_PATH Variable
     */
    private fun createExceptionReport(thread: Thread, throwable: Throwable, reporterFilesPath: File?) {
        Thread {
            reporterFilesPath?.let {
                try {
                    val currentTimestamp = getCurrentTimestamp()
                    val fileName = "${CRASH_FILE_PATH}-${currentTimestamp}.txt"
                    val fullPath = reporterFilesPath.absolutePath + File.separator + fileName
                    val writer = BufferedWriter(FileWriter(fullPath))
                    writer.append("${thread.name}\n")
                    writer.append("----------------------------------\n")
                    writer.append(currentTimestamp + "\n")
                    writer.append("----------------------------------\n")
                    writer.append("${throwable.message}\n")
                    writer.append("----------------------------------\n")
                    writer.append("${throwable.localizedMessage}\n")
                    writer.append("----------------------------------\n")
                    writer.append("${throwable.stackTraceToString()}\n")
                    writer.append("----------------------------------\n")
                    writer.flush()
                    writer.close()
                    printLogs("Crash Report File Created : $fullPath")
                } catch (ex: Exception) {
                    printLogs(ex.message ?: "", true)
                }
            }
        }.start()
    }

    fun printLogs(message: String, isError: Boolean = false) {
        println("$LOGGING_PREFIX ${if (isError) "[ERROR]" else "[DEBUG]"} $message")
    }

    private fun getCurrentTimestamp(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss", Locale.getDefault())
        return df.format(c)
    }

    private fun getNotificationMessage(thread: Thread, exception: Throwable): String {
        return "Exception Triggered During Thread Execution : ${thread.name} / Exception Message : ${exception.message}"
    }

    private fun getNotificationTitle(): String {
        return "Un Expected Exception Triggered"
    }

}
