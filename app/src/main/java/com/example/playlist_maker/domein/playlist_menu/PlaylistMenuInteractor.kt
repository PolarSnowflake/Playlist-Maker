package com.example.playlist_maker.domein.playlist_menu

import com.example.playlist_maker.domein.player.Track
import com.example.playlist_maker.domein.playlist.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistMenuInteractor {
    fun getPlaylistById(playlistId: Long): Flow<Playlist>
    fun getTracksFromPlaylist(trackIds: List<Long>): Flow<List<Track>>
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylistById(playlistId: Long)
}