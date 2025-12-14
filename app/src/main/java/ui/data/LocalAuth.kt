package com.example.smartrecipes.ui.data

import android.content.Context
import java.security.MessageDigest

object LocalAuth {

    private const val PREF_NAME = "local_auth_prefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun saveUser(context: Context, login: String, password: String) {
        val key = "user_$login"
        val hashPassword = hash(password)
        prefs(context).edit().putString(key, hashPassword).apply()
    }

    fun checkUser(context: Context, login: String, password: String): Boolean {
        val key = "user_$login"
        val stored = prefs(context).getString(key, null) ?: return false
        return stored == hash(password)
    }
}