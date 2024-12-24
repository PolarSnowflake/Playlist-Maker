package com.example.playlist_maker.ui.media_library.playlist_menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.R
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist_menu.PlaylistMenuInteractor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlaylistMenuViewModel(
    private val application: Application,
    private val interactor: PlaylistMenuInteractor
) : AndroidViewModel(application) {

    private val _playlistLiveData = MutableLiveData<Playlist?>()
    val playlistLiveData: LiveData<Playlist?> get() = _playlistLiveData

    private val _tracksLiveData = MutableLiveData<List<Track>>()
    val tracksLiveData: LiveData<List<Track>> get() = _tracksLiveData

    private val _shareText = MutableLiveData<String?>()
    val shareText: LiveData<String?> get() = _shareText

    private val _messageLiveData = MutableLiveData<String?>()
    val messageLiveData: LiveData<String?> get() = _messageLiveData

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            interactor.getPlaylistById(playlistId)
                .catch { _playlistLiveData.postValue(null) }
                .collect { playlist ->
                    _playlistLiveData.postValue(playlist)
                    loadTracks(playlist.trackIds)
                }
        }
    }

    fun loadTracks(trackIds: List<Long>) {
        viewModelScope.launch {
            interactor.getTracksFromPlaylist(trackIds)
                .catch { e -> _tracksLiveData.postValue(emptyList()) }
                .collect { tracks ->
                    _tracksLiveData.postValue(tracks)
                }
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            _playlistLiveData.value?.id?.let { playlistId ->
                interactor.removeTrackFromPlaylist(track.trackId, playlistId)
                loadPlaylist(playlistId)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistLiveData.value?.id?.let { playlistId ->
                interactor.deletePlaylistById(playlistId)
            }
        }
    }

    fun sharePlaylist() {
        val playlist = _playlistLiveData.value
        val tracks = _tracksLiveData.value

        if (playlist == null || tracks.isNullOrEmpty()) {
            _messageLiveData.postValue(application.getString(R.string.noShareMessage))
            return
        }

        val shareContent = buildString {
            append("${playlist.name}\n")
            append(playlist.description.takeIf { it.isNotEmpty() } ?: application.getString(R.string.no_description))
            append("\nКоличество треков: ${tracks.size}\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${formatTrackTime(track.trackTime ?: 0L)})\n")
            }
        }

        _shareText.postValue(shareContent)
    }

    fun formatTrackTime(trackTimeMillis: Long): String {
        val seconds = trackTimeMillis / 1000 % 60
        val minutes = trackTimeMillis / (1000 * 60)
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun clearMessages() {
        _messageLiveData.postValue(null)
        _shareText.postValue(null)
    }
}