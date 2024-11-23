package com.example.playlist_maker.domein.player

interface PlayPauseInteractor {
    fun preparePlayer(track: Track, onTimeUpdate: (String) -> Unit, onCompletion: () -> Unit)
    fun play(onTimeUpdate: (String) -> Unit)
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun seekToStart()
    fun hasReachedEnd(): Boolean
    fun getCurrentPosition(): Int
}