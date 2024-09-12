package com.example.playlist_maker.data.repository

import com.example.playlist_maker.data.dto.ItunesSearchResponse
import com.example.playlist_maker.data.network.iTunesAPI
import com.example.playlist_maker.domain.models.Track
import com.example.playlist_maker.domain.api.TrackRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(private val api: iTunesAPI) : TrackRepository {
    // Реализуем метод интерфейса с query и callback
    override fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        val call: Call<ItunesSearchResponse> = api.search(query)

        // Выполняем запрос асинхронно
        call.enqueue(object : Callback<ItunesSearchResponse> {
            override fun onResponse(
                call: Call<ItunesSearchResponse>,
                response: Response<ItunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    // Возвращаем результат через callback
                    callback(body?.results ?: emptyList())
                } else {
                    // В случае ошибки возвращаем пустой список
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                // В случае ошибки возвращаем пустой список
                callback(emptyList())
            }
        })
    }
}