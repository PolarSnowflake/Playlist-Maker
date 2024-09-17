package com.example.playlist_maker.data.search

import com.example.playlist_maker.data.player.TrackDTO
import com.google.gson.annotations.SerializedName

data class ItunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDTO>
)