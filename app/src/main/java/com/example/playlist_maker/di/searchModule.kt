package com.example.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker.data.search.ITunesAPI
import com.example.playlist_maker.data.search.SearchHistoryRepository
import com.example.playlist_maker.data.search.TrackRepositoryImpl
import com.example.playlist_maker.domein.search.SearchHistoryInteractor
import com.example.playlist_maker.domein.search.SearchHistoryInteractorImpl
import com.example.playlist_maker.domein.search.SearchTracksInteractor
import com.example.playlist_maker.domein.search.SearchTracksInteractorImpl
import com.example.playlist_maker.domein.search.TrackRepository
import com.example.playlist_maker.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single {
        get<Retrofit>().create(ITunesAPI::class.java)
    }
    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }
    single<SharedPreferences> {
        get<Context>().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(SearchHistoryRepository(get()))
    }
    single<SearchTracksInteractor> {
        SearchTracksInteractorImpl(get())
    }
    viewModel {
        SearchViewModel(get(), get())
    }
}
