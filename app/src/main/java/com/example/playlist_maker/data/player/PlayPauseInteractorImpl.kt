package com.example.playlist_maker.data.player

import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.domein.player.Track

class PlayPauseInteractorImpl(private val repository: PlayerRepository) : PlayPauseInteractor {

    override fun preparePlayer(
        track: Track,
        onTimeUpdate: (String) -> Unit,
        onCompletion: () -> Unit
    ) {
        repository.preparePlayer(track, onTimeUpdate, onCompletion)
    }

    override fun play(onTimeUpdate: (String) -> Unit) {
        repository.play(onTimeUpdate)
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun seekToStart() {
        repository.seekToStart()
    }

    override fun hasReachedEnd(): Boolean {
        return repository.hasReachedEnd()
    }
}