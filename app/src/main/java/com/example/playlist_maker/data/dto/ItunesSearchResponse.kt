package com.example.playlist_maker.data.dto

import com.example.playlist_maker.domain.models.Track
import com.google.gson.annotations.SerializedName

data class ItunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
)