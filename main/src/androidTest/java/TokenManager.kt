package com.example.labapiauth

import android.content.Context
import android.content.Context.MODE_PRIVATE

class TokenManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = !token.isNullOrEmpty()
}