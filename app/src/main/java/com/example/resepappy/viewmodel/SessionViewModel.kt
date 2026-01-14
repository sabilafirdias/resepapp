package com.example.resepappy.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    var currentUserId by mutableStateOf<Int?>(null)
        private set

    init {
        // Load dari SharedPreferences saat app dibuka
        val savedId = prefs.getInt("user_id", -1)
        if (savedId != -1) {
            currentUserId = savedId
        }
    }

    fun setUserId(id: Int) {
        currentUserId = id
        prefs.edit().putInt("user_id", id).apply()
    }

    fun clearSession() {
        currentUserId = null
        prefs.edit().remove("user_id").apply()
    }
}
