package com.example.playlist_maker.domein.settings

import androidx.lifecycle.LiveData

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun isNightModeInitialized(): Boolean {
        return repository.isNightModeInitialized()
    }

    override fun isNightMode(): LiveData<Boolean> {
        return repository.isNightMode()
    }

    override fun toggleNightMode(isNightMode: Boolean) {
        repository.toggleNightMode(isNightMode)
    }

    override fun getNightModeValue(): Boolean {
        return repository.getNightModeValue()
    }
}