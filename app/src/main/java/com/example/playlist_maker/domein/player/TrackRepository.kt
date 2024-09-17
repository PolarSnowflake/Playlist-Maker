package com.example.playlist_maker.domein.player

import com.example.playlist_maker.data.player.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit)
}