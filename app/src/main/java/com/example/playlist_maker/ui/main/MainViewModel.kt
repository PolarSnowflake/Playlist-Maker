package com.example.playlist_maker.ui.main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.ui.media_library.MediaLibraryActivity
import com.example.playlist_maker.ui.search.SearchActivity
import com.example.playlist_maker.ui.settings.SettingsActivity

class MainViewModel(private val repository: SharedPreferencesRepository) : ViewModel() {

    val themeLiveData: LiveData<Boolean> = repository.isNightMode()

    // Навигация
    fun navigateToSearch(context: Context) {
        val intent = Intent(context, SearchActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToMediateka(context: Context) {
        val intent = Intent(context, MediaLibraryActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToSettings(context: Context) {
        val intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }
}