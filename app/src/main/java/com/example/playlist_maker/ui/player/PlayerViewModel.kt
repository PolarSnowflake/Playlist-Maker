package com.example.playlist_maker.ui.player


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.data.player.PlayerState
import com.example.playlist_maker.domein.favorite_tracks.FavoriteInteractor
import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playPauseInteractor: PlayPauseInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    var currentPlaylistId: Long? = null // Для хранения текущего плейлиста

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _isTrackInPlaylist = MutableLiveData<Pair<String, Boolean>>()
    val isTrackInPlaylist: LiveData<Pair<String, Boolean>> get() = _isTrackInPlaylist

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private var playbackJob: Job? = null

    init {
        _playerState.value = PlayerState(
            isPlaying = false,
            currentPlayTime = "00:00",
            track = track
        )
        checkIfFavorite(track)
        preparePlayer()
        refreshPlaylists()
    }

    private fun checkIfFavorite(track: Track) {
        viewModelScope.launch {
            val isFav = favoriteInteractor.isTrackFavorite(track.trackId)
            _isFavorite.postValue(isFav)
        }
    }

    // Подготовка плеера
    private fun preparePlayer() {
        playPauseInteractor.preparePlayer(track, {}, ::onTrackComplete)
    }

    // Обработка нажатия Play/Pause
    fun onPlayPauseClicked() {
        val currentState = _playerState.value ?: return

        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            _playerState.value = currentState.copy(isPlaying = false)
            stopUpdatingTime()
        } else {
            if (playPauseInteractor.hasReachedEnd()) {
                playPauseInteractor.seekToStart()
            }
            playPauseInteractor.play {}
            _playerState.value = currentState.copy(isPlaying = true)
            startUpdatingTime()
        }
    }

    private fun startUpdatingTime() {
        stopUpdatingTime()
        playbackJob = viewModelScope.launch {
            while (playPauseInteractor.isPlaying()) {
                delay(300)
                val currentTime = playPauseInteractor.getCurrentPosition()
                _playerState.postValue(
                    _playerState.value?.copy(currentPlayTime = formatTime(currentTime))
                )
            }
        }
    }

    private fun stopUpdatingTime() {
        playbackJob?.cancel()
    }

    private fun onTrackComplete() {
        stopUpdatingTime()
        _playerState.value = _playerState.value?.copy(isPlaying = false, currentPlayTime = "00:00")
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteInteractor.removeTrack(track.trackId)
                _isFavorite.postValue(false)
            } else {
                favoriteInteractor.addTrack(track)
                _isFavorite.postValue(true)
            }
        }
    }

    fun addTrackToPlaylist(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            val success = playlistInteractor.addTrackToPlaylist(trackId, playlistId)
            val playlistName = playlistInteractor.getPlaylistById(playlistId)?.name ?: "Playlist"
            _isTrackInPlaylist.postValue(Pair(playlistName, success))
            refreshPlaylists()
        }
    }

    fun onImageSelected(playlistId: Long, uri: String) {
        viewModelScope.launch {
            val updated = playlistInteractor.updatePlaylistCover(playlistId, uri)
            if (!updated) {
                Log.e("PlayerViewModel", "Не удалось обновить обложку плейлиста")
            }
            refreshPlaylists()
        }
    }

    fun refreshPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists().firstOrNull() ?: emptyList()
            _playlists.postValue(playlists)
        }
    }

    // Пауза
    fun onPause() {
        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            stopUpdatingTime()
            _playerState.value = _playerState.value?.copy(isPlaying = false)
        }
    }

    fun onDestroy() {
        stopUpdatingTime()
        playPauseInteractor.release()
    }

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}