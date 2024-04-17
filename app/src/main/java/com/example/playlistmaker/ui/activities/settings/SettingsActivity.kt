package com.example.playlistmaker.ui.activities.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import com.example.playlistmaker.App
import com.example.playlistmaker.Keys
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as App

        binding.shareIV.setOnClickListener {
            val message = getString(R.string.android_developer_course)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(intent, null))
        }

        binding.supportIV.setOnClickListener {
            val message = getString(R.string.support_message)
            val messageSubject = getString(R.string.support_message_subject)
            val errorMessage = getString(R.string.error_message)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, messageSubject)
                putExtra(Intent.EXTRA_TEXT, message)
            }
            try {
                startActivity(intent)
            } catch (e:Exception){
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.agreementIV.setOnClickListener {
            val agreementUrl = getString(R.string.oferta_yandex_url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(intent)
        }

        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked) // app - alias(псевдоним) для (application as App)
            app.sharedPrefs.edit {
                putBoolean(Keys.THEME_KEY, checked)
            }

            val themeWasSavedMsg = getString(R.string.theme_was_saved)
            Toast.makeText(this, themeWasSavedMsg, Toast.LENGTH_SHORT)
                .show()
        }
    }
}

