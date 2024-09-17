package com.example.playlist_maker.presentation

import com.example.playlist_maker.data.network.iTunesAPI
import com.example.playlist_maker.data.repository.TrackRepositoryImpl
import com.example.playlist_maker.domain.api.PlayPauseInteractor
import com.example.playlist_maker.domain.api.SearchTracksInteractor
import com.example.playlist_maker.domain.api.TrackRepository
import com.example.playlist_maker.domain.impl.PlayPauseInteractorImpl
import com.example.playlist_maker.domain.impl.SearchTracksInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Creator {
    companion object {
        // Retrofit
        private fun createRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        // API-клиент
        fun createITunesAPI(): iTunesAPI {
            val retrofit = createRetrofit()
            return retrofit.create(iTunesAPI::class.java)
        }

        // Репозиторий
        fun createTrackRepository(): TrackRepository {
            return TrackRepositoryImpl(createITunesAPI())
        }

        // Поиск треков
        fun provideSearchTracksInteractor(): SearchTracksInteractor {
            val repository = createTrackRepository()
            return SearchTracksInteractorImpl(repository)
        }

        // Плей/Пауза
        fun providePlayPauseInteractor(): PlayPauseInteractor {
            return PlayPauseInteractorImpl()
        }
    }
}