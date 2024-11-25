package com.example.playlist_maker.domein.playlist

import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(trackId: Long, playlistId: Long): Boolean
    suspend fun updatePlaylistCover(playlistId: Long, uri: String): Boolean
}
