package com.example.playlist_maker.domein.settings

import androidx.lifecycle.LiveData

interface SettingsInteractor {
    fun isNightModeInitialized(): Boolean
    fun isNightMode(): LiveData<Boolean>
    fun getNightModeValue(): Boolean
    fun toggleNightMode(isNightMode: Boolean)
}