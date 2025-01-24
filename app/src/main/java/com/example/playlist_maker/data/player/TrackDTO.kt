package com.example.playlist_maker.data.player

import com.example.playlist_maker.domein.player.Track

data class TrackDTO(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) {
    fun toDomain(): Track {
        val safeReleaseDate = releaseDate ?: "Unknown"
        val safePreviewUrl = previewUrl ?: ""
        return Track(
            trackId = this.trackId,
            trackName = this.trackName,
            artistName = this.artistName,
            trackTime = this.trackTimeMillis,
            artworkUrl100 = this.artworkUrl100,
            collectionName = this.collectionName,
            releaseDate = safeReleaseDate,
            primaryGenreName = this.primaryGenreName ?: "Unknown",
            country = this.country ?: "Unknown",
            previewUrl = safePreviewUrl
        )
    }
}