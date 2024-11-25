package com.example.playlist_maker.domein.favorite_tracks

import com.example.playlist_maker.domein.player.Track

class FavoriteInteractorImpl(
    private val repository: FavoriteRepository
) : FavoriteInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun removeTrack(trackId: Long) {
        repository.removeTrackFromFavorites(trackId)
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return repository.isTrackFavorite(trackId)
    }

    override fun getAllFavoriteTracks() = repository.getFavoriteTracks()
}