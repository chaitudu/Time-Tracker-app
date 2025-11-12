package com.example.timetracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(com.example.timetracker.R.style.Theme_TimeTracker)
        super.onCreate(savedInstanceState)

        // Always go to PIN lock first
        startActivity(Intent(this, LockActivity::class.java))
        finish()
    }
}
