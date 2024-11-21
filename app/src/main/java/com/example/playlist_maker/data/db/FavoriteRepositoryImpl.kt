package com.example.playlist_maker.data.db

import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.favorite_tracks.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val dao: FavoriteTracksDao,
    private val converter: TrackConverter
) : FavoriteRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        dao.addTrack(converter.mapTrackToEntity(track))
    }

    override suspend fun removeTrackFromFavorites(trackId: Long) {
        dao.delTrack(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getFavTracks().map { entities ->
            entities.map { entity -> converter.mapEntityToTrack(entity) }
        }
    }
}