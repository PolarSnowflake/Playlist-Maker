package com.example.playlist_maker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlist_maker.R
import com.example.playlist_maker.presentation.app.App
import com.example.playlist_maker.ui.main.MainActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val darkThemeEnabled = sharedPreferences.getBoolean("DARK_THEME", false)

        themeSwitcher = findViewById(R.id.themeSwitcher)
        themeSwitcher.isChecked = darkThemeEnabled
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        //Кнопка "Назад"
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
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
        startActivity(userAgreementIntent)
    }
}