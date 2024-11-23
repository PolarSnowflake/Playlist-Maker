package com.example.playlist_maker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String, // В формате mm:ss
    val image: String, // Ссылка на обложку
    val album: String?,
    val year: String,
    val genre: String,
    val country: String,
    val previewUrl: String,
    val addedTimestamp: Long
)