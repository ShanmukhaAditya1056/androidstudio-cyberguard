package com.example.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationService {
    private const val CHANNEL_ID = "cyberguard_alerts_channel"
    private const val CHANNEL_NAME = "CyberGuard Alerts"

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "High priority active alerts for detected security threats"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showThreatAlert(context: Context, type: String, title: String, body: String) {
        // Build notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            val manager = NotificationManagerCompat.from(context)
            // check permission for API 33+
            manager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
        } catch (e: SecurityException) {
            LogService.e("NotificationService", "Failed to trigger notification due to missing permissions", e)
        }
    }
}

object LogService {
    fun e(tag: String, msg: String, tr: Throwable? = null) {
        android.util.Log.e(tag, msg, tr)
    }
    fun d(tag: String, msg: String) {
        android.util.Log.d(tag, msg)
    }
}
