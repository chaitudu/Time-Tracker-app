package com.example.timetracker.reminders

import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.timetracker.R
import com.example.timetracker.TimeTrackerApp

class ReminderWorker(
    private val ctx: Context,
    params: WorkerParameters
) : Worker(ctx, params) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Task Reminder"
        val desc = inputData.getString(KEY_DESC) ?: ""

        // ✅ Custom sound (keep your mp3 file inside res/raw folder)
        val soundUri: Uri = Uri.parse("android.resource://${ctx.packageName}/raw/reminder_sound")

        val notif = NotificationCompat.Builder(ctx, TimeTrackerApp.NOTIF_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(desc)
            .setAutoCancel(true)

            // ✅ Play sound
            .setSound(soundUri)

            // ✅ Vibration pattern
            .setVibrate(longArrayOf(0, 500, 500, 500))  // vibrate–pause–vibrate–pause

            // ✅ Priority (important for showing heads-up notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(ctx)
            .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notif)

        return Result.success()
    }

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_DESC = "desc"

        fun data(title: String, desc: String) = androidx.work.Data.Builder()
            .putString(KEY_TITLE, title)
            .putString(KEY_DESC, desc)
            .build()
    }
}
