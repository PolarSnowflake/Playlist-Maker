package com.example.playlist_maker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String
)