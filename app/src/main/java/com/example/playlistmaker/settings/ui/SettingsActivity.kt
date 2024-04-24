package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.utils.Keys
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    val app by lazy { application as App }

    private val viewModel by viewModel<SettingsViewModel>()

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

