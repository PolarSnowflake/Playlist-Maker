package com.example.playlist_maker.data.db.favoriteDB

import com.example.playlist_maker.domein.player.Track

class TrackConverter {

    fun mapTrackToEntity(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            id = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = formatTime(track.trackTime),
            image = track.artworkUrl100,
            album = track.collectionName,
            year = extractYear(track.releaseDate),
            genre = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedTimestamp = System.currentTimeMillis()
        )
    }

    fun mapEntityToTrack(entity: FavoriteTrackEntity): Track {
        return Track(
            trackId = entity.id,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = parseTimeToMillis(entity.trackTime),
            artworkUrl100 = entity.image,
            collectionName = entity.album,
            releaseDate = entity.year,
            primaryGenreName = entity.genre,
            country = entity.country,
            previewUrl = entity.previewUrl,
            isFavorite = true
        )
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun parseTimeToMillis(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toLongOrNull() ?: 0
        val seconds = parts[1].toLongOrNull() ?: 0
        return (minutes * 60 + seconds) * 1000
    }

    private fun extractYear(date: String): String {
        return date.take(4)
    }
}