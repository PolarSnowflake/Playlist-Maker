package com.example.playlist_maker.data.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedPreferencesRepository(private val application: Application) {

    private val sharedPreferences = application.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    // Проверка
    fun isNightModeInitialized(): Boolean {
        return sharedPreferences.contains("NIGHT_MODE")
    }

    // Инфа о текущем режиме
    fun isNightMode(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        liveData.value = sharedPreferences.getBoolean("NIGHT_MODE", false)
        return liveData
    }

    fun getNightModeValue(): Boolean {
        return sharedPreferences.getBoolean("NIGHT_MODE", false)
    }

    // Переключение темы
    fun toggleNightMode(isNightMode: Boolean) {
        sharedPreferences.edit().putBoolean("NIGHT_MODE", isNightMode).apply()
    }
}