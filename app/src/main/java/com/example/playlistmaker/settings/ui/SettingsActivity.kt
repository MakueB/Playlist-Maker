package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.playlistmaker.App
import com.example.playlistmaker.utils.Keys
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.SharingRepositoryImpl


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val externalNavigator by lazy { ExternalNavigatorImpl(this) }
    private val settingsRepository by lazy {
        SettingsRepositoryImpl(
            getSharedPreferences(
                Keys.PLAYLIST_MAKER_PREFERENCES,
                MODE_PRIVATE
            )
        )
    }
    private val sharingRepository by lazy { SharingRepositoryImpl(this, externalNavigator) }
    val app by lazy { application as App }

    private val viewModel: SettingsViewModel by viewModels<SettingsViewModel> {
        SettingsViewModel.getViewModelFactory(settingsRepository, sharingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.shareIV.setOnClickListener {
            viewModel.shareApp()
        }
        binding.supportIV.setOnClickListener {
            viewModel.openSupport()
        }
        binding.termsIV.setOnClickListener {
            viewModel.openTerms()
        }
        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.updateTheme(checked)
        }
    }
}

