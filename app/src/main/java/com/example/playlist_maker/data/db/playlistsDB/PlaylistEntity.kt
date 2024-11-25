package com.example.playlist_maker.data.db.playlistsDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String?,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: String,
    val trackCount: Int
)