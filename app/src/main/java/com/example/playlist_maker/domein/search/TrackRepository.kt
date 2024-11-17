package com.example.playlist_maker.domein.search

import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}