package com.example.playlist_maker.di

import com.example.playlist_maker.data.db.AppDatabase
import com.example.playlist_maker.data.db.playlistsDB.PlaylistRepositoryImpl
import com.example.playlist_maker.domein.playlist.PlaylistInteractor
import com.example.playlist_maker.domein.playlist.PlaylistInteractorImpl
import com.example.playlist_maker.domein.playlist.PlaylistRepository
import com.example.playlist_maker.ui.media_library.playlists.NewPlaylistViewModel
import com.example.playlist_maker.ui.media_library.playlists.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistsModule = module {
    single { get<AppDatabase>().playlistDao() }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { NewPlaylistViewModel(get()) }
}