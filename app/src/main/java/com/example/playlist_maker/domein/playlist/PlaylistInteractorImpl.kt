package com.example.playlist_maker.domein.playlist

import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun updatePlaylistCover(playlistId: Long, coverUri: String): Boolean {
        val playlist = repository.getPlaylistById(playlistId)
        return if (playlist != null) {
            val updatedPlaylist = playlist.copy(coverPath = coverUri)
            repository.updatePlaylist(updatedPlaylist)
            true
        } else {
            false
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun addTrackToPlaylist(trackId: Long, playlistId: Long): Boolean {
        return repository.addTrackToPlaylist(trackId, playlistId)
    }
}