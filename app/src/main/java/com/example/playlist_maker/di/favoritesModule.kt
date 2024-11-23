package com.example.playlist_maker.di

import com.example.playlist_maker.data.db.FavoriteRepositoryImpl
import com.example.playlist_maker.domein.favorite_tracks.FavoriteInteractor
import com.example.playlist_maker.domein.favorite_tracks.FavoriteInteractorImpl
import com.example.playlist_maker.domein.favorite_tracks.FavoriteRepository
import com.example.playlist_maker.ui.media_library.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }
    single<FavoriteInteractor> { FavoriteInteractorImpl(get()) }
    viewModel { FavoritesTracksViewModel(get()) }
}