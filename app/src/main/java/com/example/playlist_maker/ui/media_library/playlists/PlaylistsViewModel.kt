package com.example.playlist_maker.ui.media_library.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistInteractor

class PlaylistsViewModel(
    interactor: PlaylistInteractor
) : ViewModel() {
    val playlists: LiveData<List<Playlist>> = interactor.getAllPlaylists().asLiveData()
}