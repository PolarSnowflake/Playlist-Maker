package com.example.playlist_maker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlist_maker.domain.api.PlayPauseInteractor
import com.example.playlist_maker.domain.models.Track

class PlayPauseInteractorImpl : PlayPauseInteractor {
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var hasReachedEnd = false

    // Подготовка плеера
    override fun preparePlayer(
        track: Track,
        onTimeUpdate: (String) -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.previewUrl)
            prepare()
        }

        hasReachedEnd = false

        //Обновление текущего времени воспроизведения
        updateRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        val currentPosition = it.currentPosition / 1000
                        val formattedTime =
                            String.format("%02d:%02d", currentPosition / 60, currentPosition % 60)
                        onTimeUpdate(formattedTime)
                        handler.postDelayed(this, 1000)
                    }
                }
            }
        }

        //Слушатель завершения трека
        mediaPlayer?.setOnCompletionListener {
            hasReachedEnd = true
            onCompletion()
            updateRunnable?.let { runnable ->
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun play(onTimeUpdate: (String) -> Unit) {
        mediaPlayer?.let {
            if (hasReachedEnd) {
                seekToStart()
            }
            it.start()
            updateRunnable?.let { runnable ->
                handler.post(runnable)
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                updateRunnable?.let { runnable ->
                    handler.removeCallbacks(runnable)
                }
            }
        }
    }

    // Освобождение ресурсов
    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        updateRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
        }
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun seekToStart() {
        mediaPlayer?.seekTo(0) // Позиция воспр. в начало
        hasReachedEnd = false
    }

    override fun hasReachedEnd(): Boolean {
        return hasReachedEnd
    }
}