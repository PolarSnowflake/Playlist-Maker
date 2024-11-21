package com.example.playlist_maker.data.player

import android.media.MediaPlayer
import com.example.playlist_maker.domein.player.PlayerRepository
import com.example.playlist_maker.domein.player.Track
import java.io.IOException
import kotlinx.coroutines.*

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var hasReachedEnd = false
    private var updateJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun preparePlayer(
        track: Track,
        onTimeUpdate: (String) -> Unit,
        onCompletion: () -> Unit
    ) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepare()

            hasReachedEnd = false

            mediaPlayer.setOnCompletionListener {
                hasReachedEnd = true
                onCompletion()
                stopProgressUpdate()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun play(onTimeUpdate: (String) -> Unit) {
        if (hasReachedEnd) {
            seekToStart()
        }
        mediaPlayer.start()
        startProgressUpdate(onTimeUpdate)
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            stopProgressUpdate()
        }
    }

    override fun release() {
        stopProgressUpdate()
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun seekToStart() {
        mediaPlayer.seekTo(0)
        hasReachedEnd = false
    }

    override fun hasReachedEnd(): Boolean {
        return hasReachedEnd
    }

    private fun startProgressUpdate(onTimeUpdate: (String) -> Unit) {
        stopProgressUpdate()
        updateJob = coroutineScope.launch {
            while (isPlaying()) {
                val currentPosition = mediaPlayer.currentPosition / 1000
                val formattedTime = String.format("%02d:%02d", currentPosition / 60, currentPosition % 60)
                onTimeUpdate(formattedTime)
                delay(300L)
            }
        }
    }

    private fun stopProgressUpdate() {
        updateJob?.cancel()
        updateJob = null
    }
}