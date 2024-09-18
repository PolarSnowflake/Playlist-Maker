package com.example.playlist_maker.data.player

import com.example.playlist_maker.domein.player.Track

data class PlayerState(
    val isPlaying: Boolean,
    val currentPlayTime: String,
    val track: Track
)