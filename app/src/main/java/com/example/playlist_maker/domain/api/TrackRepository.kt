package com.example.playlist_maker.domain.api

import com.example.playlist_maker.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>) -> Unit)
}