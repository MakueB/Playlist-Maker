package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareIV = findViewById<ImageView>(R.id.shareIV)
        val supportIV = findViewById<ImageView>(R.id.supportIV)
        val agreementIV = findViewById<ImageView>(R.id.agreementIV)
        val backButton = findViewById<ImageView>(R.id.backArrow)

        shareIV.setOnClickListener {
            val message = getString(R.string.android_developer_course)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(intent, null))
        }

        supportIV.setOnClickListener {
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

        agreementIV.setOnClickListener {
            val agreementUrl = getString(R.string.oferta_yandex_url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(intent)
        }

        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }
}

