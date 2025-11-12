package com.example.timetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build

class TimeTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri: Uri = Uri.parse("android.resource://$packageName/raw/reminder_sound")

            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val ch = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Timers & Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )

            ch.setSound(soundUri, attrs)       // ✅ Enable custom sound
            ch.enableVibration(true)           // ✅ Allow vibration
            ch.vibrationPattern = longArrayOf(0, 500, 500, 500)

            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(ch)
        }
    }

    companion object {
        const val NOTIF_CHANNEL_ID = "timetracker.channel"
    }
}
