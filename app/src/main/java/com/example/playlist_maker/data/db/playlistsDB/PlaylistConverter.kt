package com.example.playlist_maker.data.db.playlistsDB

import com.example.playlist_maker.domein.playlist.Playlist

object PlaylistConverter {

    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            id = this.id,
            name = this.name ?: "",
            description = this.description ?: "",
            coverPath = this.coverImagePath ?: "",
            trackIds = this.trackIds.split(",").mapNotNull { it.toLongOrNull() },
            trackCount = this.trackCount
        )
    }

    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            id = this.id,
            name = this.name,
            description = this.description,
            coverImagePath = this.coverPath,
            trackIds = this.trackIds.joinToString(","),
            trackCount = this.trackCount
        )
    }
}
