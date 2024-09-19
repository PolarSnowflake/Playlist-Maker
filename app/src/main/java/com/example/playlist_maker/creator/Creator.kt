package com.example.playlist_maker.creator

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.example.playlist_maker.data.search.TrackRepositoryImpl
import com.example.playlist_maker.data.search.ITunesAPI
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import com.example.playlist_maker.domein.search.SearchTracksInteractorImpl
import com.example.playlist_maker.domein.search.TrackRepository
import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.domein.player.PlayPauseInteractorImpl
import com.example.playlist_maker.data.player.PlayerRepositoryImpl
import com.example.playlist_maker.domein.search.SearchHistoryInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.playlist_maker.data.search.SearchHistoryRepository
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.domein.search.SearchHistoryInteractor
import com.example.playlist_maker.domein.settings.SettingsInteractor
import com.example.playlist_maker.domein.settings.SettingsInteractorImpl

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
            val mediaPlayer = MediaPlayer()
            val playerRepository = PlayerRepositoryImpl(mediaPlayer)
            return PlayPauseInteractorImpl(playerRepository)
        }

        // История поиска
        fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
            val sharedPreferences =
                context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
            val searchHistoryRepository = SearchHistoryRepository(sharedPreferences)
            return SearchHistoryInteractorImpl(searchHistoryRepository)
        }

        // Настройки
        fun provideSettingsInteractor(context: Context): SettingsInteractor {
            val repository = SharedPreferencesRepository(context.applicationContext as Application)
            return SettingsInteractorImpl(repository)
        }
    }
}
