package com.example.playlist_maker.di

import com.example.playlist_maker.ui.media_library.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    viewModel { PlaylistsViewModel() }
}