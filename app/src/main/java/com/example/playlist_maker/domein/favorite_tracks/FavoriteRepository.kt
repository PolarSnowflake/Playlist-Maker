package com.example.playlist_maker.domein.favorite_tracks

import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(trackId: Long)
    suspend fun isTrackFavorite(trackId: Long): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
}