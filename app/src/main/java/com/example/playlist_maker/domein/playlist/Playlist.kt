package com.example.playlist_maker.domein.playlist

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String,
    val trackIds: List<Long>,
    val trackCount: Int
)