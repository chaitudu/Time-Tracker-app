package com.example.timetracker.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.timetracker.data.AppDb
import com.example.timetracker.util.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDb.get(app)
    private val userDao = db.userDao()

    val loggedInUserId = MutableLiveData<Long?>(null)

    fun login(email: String, password: String) = viewModelScope.launch {
        val user = userDao.byEmail(email)
        if (user != null && user.passwordHash == password) {

            // ✅ Save session
            SessionManager(getApplication()).saveUserId(user.id)

            loggedInUserId.postValue(user.id)
        } else {
            loggedInUserId.postValue(null)
        }
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        val id = userDao.insert(
            com.example.timetracker.data.User(email = email, passwordHash = password)
        )

        // ✅ Save session
        SessionManager(getApplication()).saveUserId(id)

        loggedInUserId.postValue(id)
    }
    fun logout() {
        SessionManager(getApplication()).logout()
        loggedInUserId.postValue(null)
    }

}

