package com.yazantarifi.android.reporter

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object ShakeReporter {

    private const val FILES_ROOT_PATH = "ShakeReporter"
    private const val CRASH_FILE_PATH = "crashes"
    private const val LOGGING_PREFIX = "[ShakeReporter]:"

    /**
     * Init The Library With Storage Path and Generated File in Private Storage for Application
     * Used To Save All Exceptions and Network Calls To See The History Via Notification
     * Here The Default Notification Channel Will Be Created
     */
    fun init(context: Context) {
        ReporterNotifications.createNotificationChannel(context)
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
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
    }

    private fun createExceptionReport(thread: Thread, throwable: Throwable, reporterFilesPath: File?) {
        Thread {
            reporterFilesPath?.let {
                try {
                    val currentTimestamp = getCurrentTimestamp()
                    val targetCrashReportFile = File(reporterFilesPath, "${CRASH_FILE_PATH}-${currentTimestamp}.txt")
                    val writer = FileWriter(targetCrashReportFile)
                    writer.append("Crash Exception Triggered On Thread : ${thread.name}")
                    writer.append("Record Timestamp : $currentTimestamp")
                    writer.append("Exception Message : ${throwable.message}")
                    writer.append("Exception Localized Message : ${throwable.localizedMessage}")
                    writer.append("Exception StackTrace : ${throwable.stackTraceToString()}")
                    writer.flush()
                    writer.close()
                    printLogs("Crash Report File Created : ${targetCrashReportFile.absoluteFile}")
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
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return df.format(c)
    }

    private fun getNotificationMessage(thread: Thread, exception: Throwable): String {
        return "Exception Triggered During Thread Execution : ${thread.name} / Exception Message : ${exception.message}"
    }

    private fun getNotificationTitle(): String {
        return "Un Expected Exception Triggered"
    }

}
