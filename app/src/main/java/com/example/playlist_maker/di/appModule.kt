package com.example.playlist_maker.di

import androidx.room.Room
import com.example.playlist_maker.data.db.AppDatabase
import com.example.playlist_maker.data.db.TrackConverter
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
    single { get<AppDatabase>().favoriteTracksDao() }
    single { TrackConverter() }
}