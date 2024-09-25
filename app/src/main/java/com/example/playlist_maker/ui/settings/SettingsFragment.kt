package com.example.playlist_maker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Изменение темы
        binding.themeSwitcher.isChecked = viewModel.isNightMode.value ?: false

        // Переключение темы
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.textView2.setOnClickListener { shareApp() }
        binding.textView3.setOnClickListener { writeSupport() }
        binding.textView4.setOnClickListener { openUserAgreement() }
    }

    // Метод для шеринга приложения
    private fun shareApp() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Метод для написания в поддержку
    private fun writeSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.share_intent_title)))
    }

    // Метод для открытия пользовательского соглашения
    private fun openUserAgreement() {
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
        startActivity(userAgreementIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}