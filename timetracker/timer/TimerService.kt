package com.example.timetracker.timer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.timetracker.R
import com.example.timetracker.TimeTrackerApp
import com.example.timetracker.ui.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _secondsLeft = MutableStateFlow(0)
    val secondsLeft = _secondsLeft.asStateFlow()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val secs = intent?.getIntExtra("seconds", 1500) ?: 1500 // default 25 mins
        startForeground(1, buildNotification(secs))
        scope.launch {
            for (s in secs downTo 0) {
                _secondsLeft.value = s
                updateNotification(s)
                delay(1000L)
            }
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun buildNotification(secs: Int): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, TimeTrackerApp.NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Focus timer running")
            .setContentText("Time left: ${secs/60}m ${secs%60}s")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(secs: Int) {
        val nm = getSystemService(android.app.NotificationManager::class.java)
        nm.notify(1, buildNotification(secs))
    }
}
