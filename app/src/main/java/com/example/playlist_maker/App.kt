package com.example.playlist_maker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

        // Если значение не установлено, установить его в соответствии с системной темой
        if (!sharedPreferences.contains("DARK_THEME")) {
            val isSystemDark = (resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("DARK_THEME", isSystemDark).apply()
        }

        applyTheme()
    }

    // Переключение темы
    fun switchTheme(darkThemeEnabled: Boolean) {
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("DARK_THEME", darkThemeEnabled).apply()
        applyTheme()
    }

    // Применение темы
    private fun applyTheme() {
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean("DARK_THEME", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}