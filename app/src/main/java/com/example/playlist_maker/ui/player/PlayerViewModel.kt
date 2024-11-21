package com.example.playlist_maker.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.data.player.PlayerState
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.domein.favorite_tracks.FavoriteInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playPauseInteractor: PlayPauseInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var playbackJob: Job? = null

    init {
        _playerState.value = PlayerState(
            isPlaying = false,
            currentPlayTime = "00:00",
            track = track
        )
        _isFavorite.value = track.isFavorite // Устанавливаем начальное состояние
        preparePlayer()
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
        if (playbackJob?.isActive == true) return // Корутину не запускаем повторно
        playbackJob = viewModelScope.launch {
            while (playPauseInteractor.isPlaying()) {
                val currentTime = playPauseInteractor.getCurrentPosition()
                val formattedTime = formatTime(currentTime)
                updatePlayTime(formattedTime)
                delay(500) // Обновление каждые 500 мс
            }
        }
    }

    private fun stopUpdatingTime() {
        playbackJob?.cancel()
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

    private fun updatePlayTime(formattedTime: String) {
        val currentState = _playerState.value ?: return
        _playerState.postValue(currentState.copy(currentPlayTime = formattedTime))
    }

    // Завершение трека
    private fun onTrackComplete() {
        stopUpdatingTime()
        val currentState = _playerState.value ?: return
        _playerState.postValue(currentState.copy(isPlaying = false, currentPlayTime = "00:00"))
    }

    // Пауза
    fun onPause() {
        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            val currentState = _playerState.value ?: return
            _playerState.value = currentState.copy(isPlaying = false)
            stopUpdatingTime()
        }
    }

    fun onDestroy() {
        stopUpdatingTime()
        playPauseInteractor.release()
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}