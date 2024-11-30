package com.example.playlist_maker.di

import android.media.MediaPlayer
import com.example.playlist_maker.data.player.PlayerRepositoryImpl
import com.example.playlist_maker.domein.player.PlayPauseInteractor
import com.example.playlist_maker.domein.player.PlayPauseInteractorImpl
import com.example.playlist_maker.domein.player.PlayerRepository
import com.example.playlist_maker.ui.player.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    single<PlayPauseInteractor> { PlayPauseInteractorImpl(get()) }
    viewModel { (track: com.example.playlist_maker.domein.player.Track) ->
        PlayerViewModel(track, get(), get(), get())
    }
}