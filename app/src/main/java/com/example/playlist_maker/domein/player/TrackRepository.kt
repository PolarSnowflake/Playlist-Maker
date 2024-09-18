package com.example.playlist_maker.domein.player

interface TrackRepository {
    fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit)
}