package com.example.timetracker.util

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserId(id: Long) {
        prefs.edit().putLong("user_id", id).apply()
    }

    fun getUserId(): Long? {
        val id = prefs.getLong("user_id", -1)
        return if (id == -1L) null else id
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    // ✅ Save PIN
    fun saveAppPin(pin: String) {
        prefs.edit().putString("app_pin", pin).apply()
    }

    // ✅ Read PIN (default = 1234)
    fun getAppPin(): String {
        return prefs.getString("app_pin", "1234")!!
    }
}
