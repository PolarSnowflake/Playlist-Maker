package com.example.playlist_maker.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlist_maker.domein.player.PlayerRepository
import com.example.playlist_maker.domein.player.Track
import java.io.IOException

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var hasReachedEnd = false

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

            updateRunnable = object : Runnable {
                override fun run() {
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition / 1000
                        val formattedTime =
                            String.format("%02d:%02d", currentPosition / 60, currentPosition % 60)
                        onTimeUpdate(formattedTime)
                        handler.postDelayed(this, 1000)
                    }
                }
            }

            mediaPlayer.setOnCompletionListener {
                hasReachedEnd = true
                onCompletion()
                updateRunnable?.let { runnable ->
                    handler.removeCallbacks(runnable)
                }
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
        updateRunnable?.let { runnable ->
            handler.post(runnable)
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            updateRunnable?.let { runnable ->
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun release() {
        mediaPlayer.release()
        updateRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
        }
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
}