package com.example.playlist_maker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.domein.settings.SettingsInteractor
import com.example.playlist_maker.domein.settings.SettingsInteractorImpl
import com.example.playlist_maker.domein.settings.SettingsRepository
import com.example.playlist_maker.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
    }
    single<SettingsRepository> { SharedPreferencesRepository(get()) }
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }
    viewModel { SettingsViewModel(get()) }
    single { SharedPreferencesRepository(androidContext().applicationContext as Application) }
}