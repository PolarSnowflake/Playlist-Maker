package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.dto.ItunesSearchResponse
import com.example.playlist_maker.data.dto.TrackDTO
import com.example.playlist_maker.data.network.iTunesAPI
import com.example.playlist_maker.domain.api.TrackRepository
import com.example.playlist_maker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val api: iTunesAPI) : TrackRepository {
    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        api.search(query).enqueue(object : Callback<ItunesSearchResponse> {
            override fun onResponse(
                call: Call<ItunesSearchResponse>,
                response: Response<ItunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                        val tracks =
                            searchResponse.results.map { trackDto: TrackDTO -> trackDto.toDomain() }
                        callback(Result.success(tracks))
                    } else {
                        callback(Result.success(emptyList())) // Если результат пуст, возвращаем пустой список
                    }
                } else {
                    callback(Result.failure(Exception()))
                }
            }

            override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}