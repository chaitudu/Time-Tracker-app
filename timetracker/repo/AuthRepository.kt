package com.example.timetracker.repo

import android.content.Context
import android.util.Base64
import com.example.timetracker.data.AppDb
import com.example.timetracker.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class AuthRepository(private val ctx: Context) {
    private val userDao = AppDb.get(ctx).userDao()
    private val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)

    suspend fun signUp(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val existing = userDao.byEmail(email)
        if (existing != null) return@withContext Result.failure(IllegalArgumentException("Email already registered"))
        val hash = sha256(password)
        userDao.insert(User(email = email, passwordHash = hash))
        prefs.edit().putString("email", email).apply()
        Result.success(Unit)
    }

    suspend fun signIn(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.byEmail(email) ?: return@withContext Result.failure(IllegalArgumentException("User not found"))
        if (user.passwordHash == sha256(password)) {
            prefs.edit().putString("email", email).apply()
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid credentials"))
        }
    }

    fun isLoggedIn(): Boolean = prefs.getString("email", null) != null
    fun logout() { prefs.edit().clear().apply() }

    private fun sha256(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(s.toByteArray())
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}
