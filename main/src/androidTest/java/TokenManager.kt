package com.example.yourapp.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            if (value == null) {
                prefs.edit().remove(KEY_TOKEN).apply()
                _isLoggedIn.value = false
            } else {
                prefs.edit().putString(KEY_TOKEN, value).apply()
                _isLoggedIn.value = true
            }
        }

    fun clear() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
    }

    fun checkAndUpdateLoginState() {
        _isLoggedIn.value = !token.isNullOrEmpty()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}