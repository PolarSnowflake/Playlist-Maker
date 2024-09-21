package com.example.playlist_maker.data.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlist_maker.domein.settings.SettingsInteractor
import com.example.playlist_maker.domein.settings.SettingsRepository

class SharedPreferencesRepository(private val application: Application) : SettingsRepository {

    private val sharedPreferences =
        application.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    // Проверка
    override fun isNightModeInitialized(): Boolean {
        return sharedPreferences.contains("NIGHT_MODE")
    }

    // Инфа о текущем режиме
    override fun isNightMode(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = sharedPreferences.getBoolean("NIGHT_MODE", false)
        return liveData
    }

    override fun getNightModeValue(): Boolean {
        return sharedPreferences.getBoolean("NIGHT_MODE", false)
    }

    // Переключение темы
    override fun toggleNightMode(isNightMode: Boolean) {
        sharedPreferences.edit().putBoolean("NIGHT_MODE", isNightMode).apply()
    }
}