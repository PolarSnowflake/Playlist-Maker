package com.example.playlist_maker.domein.search

import com.example.playlist_maker.data.player.Track

interface SearchTracksInteractor {
    fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit)
}