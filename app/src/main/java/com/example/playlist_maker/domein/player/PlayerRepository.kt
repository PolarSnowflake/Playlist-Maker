package com.example.playlist_maker.domein.player

import com.example.playlist_maker.domein.player.Track

interface PlayerRepository {
    fun preparePlayer(track: Track, onTimeUpdate: (String) -> Unit, onCompletion: () -> Unit)
    fun play(onTimeUpdate: (String) -> Unit)
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun seekToStart()
    fun hasReachedEnd(): Boolean
}