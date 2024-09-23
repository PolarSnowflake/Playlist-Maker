package com.example.playlist_maker.di

import com.example.playlist_maker.ui.media_library.FavoritesTracksViewModel
import com.example.playlist_maker.ui.media_library.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { FavoritesTracksViewModel() }
    viewModel { PlaylistsViewModel() }
}