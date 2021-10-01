package com.yazantarifi.android.reporter

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import com.yazantarifi.android.reporter.screens.ShakeReporterScreen


object ShakeReporter {

    var networkRequestsFile: File? = null
    private const val FILES_ROOT_PATH = "ShakeReporter"
    private const val LOGGING_PREFIX = "[ShakeReporter]:"
    private const val NETWORK_REQUESTS_FILE_NAME = "Network-Requests"
    const val CRASH_FILE_PATH = "crashes"
    const val PATH_SPLITTER = "-----"

    /**
     * Init Shake Sensor In Any Screen
     * The Shake Sensor Once Triggered Will Start the Reporter Screen
     * To Show All Data In History for Crashes, Network Requests
     * Usually Add this Line in Base Screen in Your Application
     * To Be Handled in All Screens In Debug Mode or Dev Build
     */
    @JvmStatic
    fun startSensorListener(context: Context, listener: ShakeSensorListener) {
        (context.getSystemService(SENSOR_SERVICE) as? SensorManager)?.let {
            it.registerListener(listener, it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * Use This Method in OnDestroy of the Activity To Remove the Shake Listener
     * and Un Register The Listeners of the Sensor
     */
    @JvmStatic
    fun destroySensorListener(context: Context, listener: ShakeSensorListener) {
        (context.getSystemService(SENSOR_SERVICE) as? SensorManager)?.let {
            it.unregisterListener(listener)
        }
    }

    /**
     * Init The Library With Storage Path and Generated File in Private Storage for Application
     * Used To Save All Exceptions and Network Calls To See The History Via Notification
     * Here The Default Notification Channel Will Be Created
     */
    @JvmStatic
    fun init(context: Context) {
        createNetworkRequestsFile(getShakeReporterRootFile(context))
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
        val reporterFilesPath = getShakeReporterRootFile(context)
        createExceptionReport(thread, throwable, reporterFilesPath)
        ReporterNotifications.showNotification(
            getNotificationTitle(),
            getNotificationMessage(thread, throwable),
            context
        )
    }

    private fun getShakeReporterRootFile(context: Context): File {
        val reporterFilesPath = File(context.filesDir, FILES_ROOT_PATH)
        if (!reporterFilesPath.exists()) {
            reporterFilesPath.mkdir()
            printLogs("Create Root Crashes Path : ${reporterFilesPath.absoluteFile}")
        }

        return reporterFilesPath
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

                    var stackTraceString = ""
                    throwable.stackTrace.forEach {
                        stackTraceString += it.toString() + "\n"
                    }

                    writer.append("${stackTraceString}\n")
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

    /**
     * This Method Will Create Network Calls File
     * This File Will Have The Whole Logs for The Application
     * This Logs Accessible By Shake Listener To Shake the Device
     * Then Open List Screen and Each Request Will be Seperated By -----
     */
    private fun createNetworkRequestsFile(reporterFilesPath: File?) {
        Thread {
            try {
                reporterFilesPath?.let {
                    networkRequestsFile = File(it.absolutePath, NETWORK_REQUESTS_FILE_NAME).apply {
                        if (!this.exists()) {
                            this.createNewFile()
                            printLogs("Network Requests File Created : ${this.absolutePath}")
                        }
                    }
                }
            } catch (ex: Exception) {
                printLogs(ex.message ?: "", true)
            }
        }.start()
    }

    /**
     * Logger for Printing Logs Specified By the Library
     * By Specific Tag @see LOGGING_PREFIX
     */
    fun printLogs(message: String, isError: Boolean = false) {
        println("$LOGGING_PREFIX ${if (isError) "[ERROR]" else "[DEBUG]"} $message")
    }

    /**
     * Each Crash File is Reported In Specific File
     * This Structure for Crashes Only Because Each App
     * Can Have Like Max 10 Crashes Per Session and Could be Managed in One Session
     */
    private fun getCurrentTimestamp(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss", Locale.getDefault())
        return df.format(c)
    }

    /**
     * Get The Static String In Each Catched Crash to Show Notification
     */
    private fun getNotificationMessage(thread: Thread, exception: Throwable): String {
        return "Exception Triggered During Thread Execution : ${thread.name} / Exception Message : ${exception.message}"
    }

    private fun getNotificationTitle(): String {
        return "Un Expected Exception Triggered"
    }

}
