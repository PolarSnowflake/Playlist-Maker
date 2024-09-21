package com.example.playlist_maker.data.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlist_maker.domein.player.PlayerRepository
import com.example.playlist_maker.domein.player.Track

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var hasReachedEnd = false

    override fun preparePlayer(
        track: Track,
        onTimeUpdate: (String) -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer.apply {
            setDataSource(track.previewUrl)
            prepare()
        }

        hasReachedEnd = false

        updateRunnable = object : Runnable {
            override fun run() {
                mediaPlayer.let {
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

        mediaPlayer.setOnCompletionListener {
            hasReachedEnd = true
            onCompletion()
            updateRunnable?.let { runnable ->
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun play(onTimeUpdate: (String) -> Unit) {
        mediaPlayer.let {
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
        mediaPlayer.let {
            if (it.isPlaying) {
                it.pause()
                updateRunnable?.let { runnable ->
                    handler.removeCallbacks(runnable)
                }
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