package com.example.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.data.settings.SharedPreferencesRepository
import com.example.playlist_maker.di.mediaLibraryModule
import com.example.playlist_maker.di.playerModule
import com.example.playlist_maker.di.searchModule
import com.example.playlist_maker.di.settingsModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    private val sharedPreferencesRepository: SharedPreferencesRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(playerModule,searchModule,settingsModule,mediaLibraryModule))
        }

        // Если значение не установлено, установить его в соответствии с системной темой
        if (!sharedPreferencesRepository.isNightModeInitialized()) {
            val isSystemDark = (resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            sharedPreferencesRepository.toggleNightMode(isSystemDark)
        }

        applyTheme()
    }

    // Применение темы
    private fun applyTheme() {
        val darkTheme = sharedPreferencesRepository.getNightModeValue()
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}