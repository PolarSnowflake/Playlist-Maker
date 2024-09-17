package com.example.playlist_maker.creator

import com.example.playlist_maker.data.player.TrackRepositoryImpl
import com.example.playlist_maker.data.search.ITunesAPI
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import com.example.playlist_maker.domein.search.SearchTracksInteractorImpl
import com.example.playlist_maker.domein.player.TrackRepository
import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.data.player.PlayPauseInteractorImpl
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
        fun createITunesAPI(): ITunesAPI {
            val retrofit = createRetrofit()
            return retrofit.create(ITunesAPI::class.java)
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