package com.example.smartrecipes.ui.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK = "is_dark_mode"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDark(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK, false)

    fun setDark(context: Context, dark: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK, dark).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun applyTheme(context: Context) {
        val dark = isDark(context)
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}