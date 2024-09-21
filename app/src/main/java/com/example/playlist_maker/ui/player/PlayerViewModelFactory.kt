package com.example.playlist_maker.ui.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.domein.player.Track

class PlayerViewModelFactory(
    private val track: Track,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            val playPauseInteractor = Creator.providePlayPauseInteractor()
            return PlayerViewModel(track, playPauseInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}