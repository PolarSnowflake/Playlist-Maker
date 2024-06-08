package com.example.playlist_maker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("NIGHT_MODE", false)
        if (isNightMode) {
            setTheme(R.style.Theme_PlaylistMaker_Dark)
        } else {
            setTheme(R.style.Theme_PlaylistMaker_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switch: Switch = findViewById(R.id.switch1)
        switch.isChecked = isNightMode

        switch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("NIGHT_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_PlaylistMaker_Dark)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_PlaylistMaker_Light)
            }
            recreate()  // перезапускаем активность
        }

        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    // Метод для шеринга приложения
    fun shareApp(view: View) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Метод для написания в поддержку
    fun writeSupport(view: View) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
        }

        startActivity(Intent.createChooser(emailIntent, getString(R.string.share_intent_title)))
    }

    // Метод для открытия пользовательского соглашения
    fun openUserAgreement(view: View) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
        startActivity(userAgreementIntent)
    }
}