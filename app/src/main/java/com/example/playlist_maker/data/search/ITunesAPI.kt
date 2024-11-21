package com.example.playlist_maker.data.search

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesAPI {
    @GET("/search")
    suspend fun search(
        @Query("term") searchTerm: String,
        @Query("media") mediaType: String = "music",
        @Query("entity") entityType: String = "song"
    ): ItunesSearchResponse
}