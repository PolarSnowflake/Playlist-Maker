package com.example.playlist_maker.domein.favorite_tracks

import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(trackId: Long)
    suspend fun isTrackFavorite(trackId: Long): Boolean
    fun getAllFavoriteTracks(): Flow<List<Track>>
}