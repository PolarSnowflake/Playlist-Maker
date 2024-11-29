package com.example.playlist_maker.data.db.playlistsDB

import com.example.playlist_maker.data.db.playlistsDB.PlaylistConverter.toPlaylist
import com.example.playlist_maker.data.db.playlistsDB.PlaylistConverter.toPlaylistEntity
import com.example.playlist_maker.domein.playlist.Playlist
import com.example.playlist_maker.domein.playlist.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val dao: PlaylistDao) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        val entity = playlist.toPlaylistEntity()
        return dao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlist.toPlaylistEntity()
        dao.updatePlaylist(entity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getAllPlaylists().map { entities ->
            entities.map { it.toPlaylist() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = dao.getPlaylistById(id)
        return entity?.toPlaylist()
    }

    override suspend fun addTrackToPlaylist(trackId: Long, playlistId: Long): Boolean {
        val playlist = dao.getPlaylistById(playlistId)?.toPlaylist()
            ?: throw IllegalArgumentException("Playlist not found")
        if (playlist.trackIds.contains(trackId)) {
            return false // Track already exists in the playlist
        }
        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds + trackId,
            trackCount = playlist.trackCount + 1
        )
        dao.updatePlaylist(updatedPlaylist.toPlaylistEntity())
        return true
    }

    override suspend fun updatePlaylistCover(playlistId: Long, uri: String): Boolean {
        val playlist = dao.getPlaylistById(playlistId)?.toPlaylist()
            ?: throw IllegalArgumentException("Playlist not found")
        val updatedPlaylist = playlist.copy(coverPath = uri)
        dao.updatePlaylist(updatedPlaylist.toPlaylistEntity())
        return true
    }
}