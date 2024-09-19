package com.example.playlist_maker.domein.settings

import androidx.lifecycle.LiveData

interface SettingsRepository {
    fun isNightModeInitialized(): Boolean
    fun isNightMode(): LiveData<Boolean>
    fun getNightModeValue(): Boolean
    fun toggleNightMode(isNightMode: Boolean)
}