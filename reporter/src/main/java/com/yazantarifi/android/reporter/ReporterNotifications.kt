package com.yazantarifi.android.reporter

import android.content.Context
import android.app.NotificationManager

import android.app.NotificationChannel

import android.app.PendingIntent
import android.content.Intent

import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yazantarifi.android.reporter.screens.ShakeReporterScreen
import java.lang.Exception
import kotlin.random.Random


/**
 * Notifications Information Used to Create Notifications for Library
 * This Notification Channel is Used to Display Notifications in Any Application
 * Used One Time Once Library Initialized Then Will Use it Once Exception Triggered
 */
object ReporterNotifications {

    private const val NOTIFICATION_NAME = "Reporter Notifications"
    private const val NOTIFICATION_ID = "ReporterNotificationsChannel"
    private const val NOTIFICATION_DESCRIPTION = """
        Shake Reporter Android Library Used To Send Reports During Development Phase
        In Order To Show Notifications On Un Catch Exceptions on Thread Class
        On Notification Click on it Will Show You The History of Exceptions
        and OkHttp Requests By Applying Interceptor
    """

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, importance)
            channel.description = NOTIFICATION_DESCRIPTION
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
            ShakeReporter.printLogs("Notification Channel Created ...")
        }
    }

    fun showNotification(title: String, message: String, context: Context) {
        try {
            val intent = Intent(context, ShakeReporterScreen::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val builder = NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)

            with(NotificationManagerCompat.from(context)) {
                notify(getNotificationId(), builder.build())
            }

            ShakeReporter.printLogs("Notification Displayed ...")
        } catch (ex: Exception) {
            ShakeReporter.printLogs(ex?.message ?: "", true)
        }
    }

    private fun getNotificationId(): Int {
        return Random.nextInt(1000)
    }

}
