package com.xaarlox.task11_microgrid_jwt_auth.data.auth

import android.content.Context
import androidx.core.content.edit

/**
 * Local token storage. SharedPreferences is synchronous, so the Interceptor
 * (running on the OkHttp thread) can retrieve the token immediately. For a production
 * app, the token should be encrypted with a key from the Android Keystore
 */
class TokenStorage(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun read(): String? = prefs.getString(KEY_TOKEN, null)
    fun save(token: String) = prefs.edit { putString(KEY_TOKEN, token) }
    fun clear() = prefs.edit { remove(KEY_TOKEN) }

    private companion object {
        const val KEY_TOKEN = "jwt"
    }
}