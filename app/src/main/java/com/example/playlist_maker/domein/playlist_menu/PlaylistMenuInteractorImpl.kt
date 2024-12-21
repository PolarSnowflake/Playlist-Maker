package com.example.playlist_maker.domein.playlist_menu

import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistRepository
import com.example.playlist_maker.domein.search.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class PlaylistMenuInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository
) : PlaylistMenuInteractor {

    override fun getPlaylistById(playlistId: Long): Flow<Playlist> {
        return playlistRepository.getPlaylistByIdFlow(playlistId)
    }

    override fun getTracksFromPlaylist(trackIds: List<Long>): Flow<List<Track>> {
        return flow {
            val tracks = trackIds.mapNotNull { id ->
                trackRepository.searchTracks(id.toString())
                    .firstOrNull()?.getOrNull()?.firstOrNull()
            }
            emit(tracks)
        }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylistById(playlistId: Long) {
        playlistRepository.deletePlaylistById(playlistId)
    }
}