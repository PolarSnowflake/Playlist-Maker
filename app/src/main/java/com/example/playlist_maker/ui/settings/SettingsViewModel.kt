package com.example.playlist_maker.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.settings.SharedPreferencesRepository

class SettingsViewModel(private val repository: SharedPreferencesRepository) : ViewModel() {

    val isNightMode: LiveData<Boolean> = repository.isNightMode()

    // Переключение темы
    fun switchTheme(isNightMode: Boolean) {
        // Проверка нужно ли переключать тему
        if (repository.getNightModeValue() != isNightMode) {
            repository.toggleNightMode(isNightMode)

            // Применяем тему только при необходимости
            AppCompatDelegate.setDefaultNightMode(
                if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}