package com.example.playlist_maker.domein.favorite_tracks

import com.example.playlist_maker.domein.player.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val repository: FavoriteRepository
) : FavoriteInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun removeTrack(trackId: Long) {
        repository.removeTrackFromFavorites(trackId)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}