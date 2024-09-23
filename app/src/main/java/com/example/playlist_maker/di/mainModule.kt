package com.example.playlist_maker.di

import android.app.Application
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { SharedPreferencesRepository(androidContext().applicationContext as Application) }
    viewModel { MainViewModel(get()) }
}