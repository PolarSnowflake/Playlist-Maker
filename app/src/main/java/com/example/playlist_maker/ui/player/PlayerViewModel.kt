package com.example.playlist_maker.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.player.PlayerState
import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.player.PlayPauseInteractor

class PlayerViewModel(
    private val track: Track,
    private val playPauseInteractor: PlayPauseInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    init {
        _playerState.value = PlayerState(
            isPlaying = false,
            currentPlayTime = "00:00",
            track = track
        )
        preparePlayer()
    }

    // Подготовка плеера
    private fun preparePlayer() {
        playPauseInteractor.preparePlayer(track, ::updatePlayTime, ::onTrackComplete)
    }

    // Обработка нажатия Play/Pause
    fun onPlayPauseClicked() {
        val currentState = _playerState.value ?: return

        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            _playerState.value = currentState.copy(isPlaying = false)
        } else {
            if (playPauseInteractor.hasReachedEnd()) {
                playPauseInteractor.seekToStart()
            }
            playPauseInteractor.play(::updatePlayTime)
            _playerState.value = currentState.copy(isPlaying = true)
        }
    }

    // Обновление времени воспроизведения
    private fun updatePlayTime(formattedTime: String) {
        val currentState = _playerState.value ?: return
        _playerState.postValue(currentState.copy(currentPlayTime = formattedTime))
    }

    // Завершение трека
    private fun onTrackComplete() {
        val currentState = _playerState.value ?: return
        _playerState.postValue(currentState.copy(isPlaying = false, currentPlayTime = "00:00"))
    }

    // Пауза
    fun onPause() {
        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            val currentState = _playerState.value ?: return
            _playerState.value = currentState.copy(isPlaying = false)
        }
    }

    fun onDestroy() {
        playPauseInteractor.release()
    }
}