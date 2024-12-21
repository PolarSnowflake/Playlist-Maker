package com.example.playlist_maker.di

import android.app.Application
import com.example.playlist_maker.data.db.playlistsDB.PlaylistRepositoryImpl
import com.example.playlist_maker.domein.playlist.PlaylistRepository
import com.example.playlist_maker.domein.playlist_menu.PlaylistMenuInteractor
import com.example.playlist_maker.domein.playlist_menu.PlaylistMenuInteractorImpl
import com.example.playlist_maker.ui.media_library.playlist_menu.PlaylistMenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playlistMenuModule = module {
    viewModel { (application: Application) ->
        PlaylistMenuViewModel(application, get())
    }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get()) }
    single<PlaylistMenuInteractor> { PlaylistMenuInteractorImpl(get(), get()) }

}