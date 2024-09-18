package com.example.playlist_maker.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.domein.settings.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    val isNightMode: LiveData<Boolean> = settingsInteractor.isNightMode()

    // Переключение темы
    fun switchTheme(isNightMode: Boolean) {
        if (settingsInteractor.getNightModeValue() != isNightMode) {
            settingsInteractor.toggleNightMode(isNightMode)

            // Применяем тему
            AppCompatDelegate.setDefaultNightMode(
                if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}