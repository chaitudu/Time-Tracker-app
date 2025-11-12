package com.example.timetracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timetracker.databinding.ActivityLockBinding
import com.example.timetracker.util.SessionManager

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        binding.btnSubmit.setOnClickListener {
            val pin = binding.edPin.text.toString()
            val savedPin = session.getAppPin()  // ✅ load stored PIN

            if (pin == savedPin) {
                val uid = session.getUserId()

                val next = if (uid != null)
                    MainActivity::class.java
                else
                    AuthActivity::class.java

                startActivity(Intent(this, next))
                finish()

            } else {
                binding.txtTitle.text = "Wrong PIN! Try again"
            }
        }
    }
}
