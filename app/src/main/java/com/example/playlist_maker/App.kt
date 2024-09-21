package com.example.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.data.settings.SharedPreferencesRepository

class App : Application() {

    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    override fun onCreate() {
        super.onCreate()

        sharedPreferencesRepository = SharedPreferencesRepository(this)

        // Если значение не установлено, установить его в соответствии с системной темой
        if (!sharedPreferencesRepository.isNightModeInitialized()) {
            val isSystemDark = (resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            sharedPreferencesRepository.toggleNightMode(isSystemDark)
        }

        applyTheme()
    }

    // Переключение темы
    fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPreferencesRepository.toggleNightMode(darkThemeEnabled)
        applyTheme()
    }

    // Применение темы
    private fun applyTheme() {
        val darkTheme = sharedPreferencesRepository.getNightModeValue()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}