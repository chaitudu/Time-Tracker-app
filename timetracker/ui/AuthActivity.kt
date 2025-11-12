package com.example.timetracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timetracker.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply app theme after splash
        setTheme(R.style.Theme_TimeTracker)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Load login fragment only first time
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_container, LoginFragment())
                .commit()
        }
    }
}
