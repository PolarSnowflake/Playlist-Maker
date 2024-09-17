package com.example.playlist_maker.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.data.player.Track
import com.example.playlist_maker.domein.player.PlayPauseInteractor

class PlayerViewModel(
    private val track: Track,
    private val playPauseInteractor: PlayPauseInteractor
) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPlayTime = MutableLiveData<String>()
    val currentPlayTime: LiveData<String> get() = _currentPlayTime

    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> get() = _trackData

    init {
        _trackData.value = track
        preparePlayer()
    }

    // Подготовка плеера
    private fun preparePlayer() {
        playPauseInteractor.preparePlayer(track, ::updatePlayTime, ::onTrackComplete)
    }

    // Обработка нажатия Play/Pause
    fun onPlayPauseClicked() {
        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            _isPlaying.value = false
        } else {
            if (playPauseInteractor.hasReachedEnd()) {
                playPauseInteractor.seekToStart()
            }
            playPauseInteractor.play(::updatePlayTime)
            _isPlaying.value = true
        }
    }

    // Обновление времени воспр.
    private fun updatePlayTime(formattedTime: String) {
        _currentPlayTime.postValue(formattedTime)
    }

    // Завершение трека
    private fun onTrackComplete() {
        _isPlaying.postValue(false)
        _currentPlayTime.postValue("00:00") // Сброс времени
    }

    // Пауза
    fun onPause() {
        if (playPauseInteractor.isPlaying()) {
            playPauseInteractor.pause()
            _isPlaying.value = false
        }
    }

    fun onDestroy() {
        playPauseInteractor.release()
    }
}