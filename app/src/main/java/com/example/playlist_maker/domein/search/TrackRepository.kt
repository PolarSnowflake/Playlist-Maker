package com.example.playlist_maker.domein.search

import com.example.playlist_maker.domein.player.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit)
}