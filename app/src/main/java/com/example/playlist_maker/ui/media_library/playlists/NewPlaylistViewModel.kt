package com.example.playlist_maker.ui.media_library.playlists

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    fun createPlaylist(name: String, description: String?, coverImageUri: Uri?) {
        val playlist = Playlist(
            id = 0,
            name = name,
            description = description ?: "",
            coverPath = coverImageUri?.toString() ?: "",
            trackIds = emptyList(),
            trackCount = 0
        )
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        Log.d("NewPlaylistViewModel", "Updating playlist in ViewModel: $playlist")
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun getPlaylistById(playlistId: Long): LiveData<Playlist?> {
        val liveData = MutableLiveData<Playlist?>()
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            liveData.postValue(playlist)
        }
        return liveData
    }
}