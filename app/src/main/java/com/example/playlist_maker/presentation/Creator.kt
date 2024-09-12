package com.example.playlist_maker.presentation

import com.example.playlist_maker.data.network.iTunesAPI
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.domain.api.TrackRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Creator {
    companion object {
        // Создаём Retrofit
        private fun createRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // Сделаем метод createITunesAPI() публичным
        fun createITunesAPI(): iTunesAPI {
            val retrofit = createRetrofit()
            return retrofit.create(iTunesAPI::class.java)
        }

        // Создаём репозиторий
        fun createTrackRepository(): TrackRepository {
            return TrackRepositoryImpl(createITunesAPI())
        }
    }
}