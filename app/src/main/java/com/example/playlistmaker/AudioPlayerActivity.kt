package com.example.playlistmaker

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
    private fun convert(timeInMillis: Long) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeInMillis)

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val countryTextView = findViewById<TextView>(R.id.country)
        val genreTextView = findViewById<TextView>(R.id.genre)
        val releaseDateTextView = findViewById<TextView>(R.id.releaseDate)
        val collectionNameTextView = findViewById<TextView>(R.id.collectionName)
        val durationTextView = findViewById<TextView>(R.id.duration)
        val collectionNameBigTextView = findViewById<TextView>(R.id.collectionNameTextView)
        val trackNameTextView = findViewById<TextView>(R.id.trackName)
        val coverImageView = findViewById<ImageView>(R.id.cover)
        val addButton = findViewById<ImageButton>(R.id.addButton)
        val playButton = findViewById<ImageButton>(R.id.playButton)
        val likeButton = findViewById<ImageButton>(R.id.likeButton)
        val backButton = findViewById<ImageView>(R.id.backArrow)
        val collectionNameGroup = findViewById<Group>(R.id.albumGroup)

        //val track = intent.getParcelableExtra<Track>("track")
        val track = intent.parcelable<Track>("track")

        countryTextView.setText(track?.country) ?: ""
        genreTextView.setText(track?.primaryGenreName) ?: ""
        releaseDateTextView.setText(track?.releaseDate?.substring(0, 4)) ?: ""

        if (!track?.collectionName.isNullOrEmpty()) {
            collectionNameGroup.isVisible = true
            collectionNameTextView.setText(track?.collectionName) ?: ""
        } else {
            collectionNameGroup.isVisible = false
        }
        durationTextView.setText(convert(track?.trackTimeMillis ?: 0))
        collectionNameBigTextView.setText(track?.collectionName) ?: ""
        trackNameTextView.setText(track?.trackName) ?: ""

        val cornersInPx = dpToPx(2f, this)

        if (track?.artworkUrl100.isNullOrEmpty())
            coverImageView.setImageResource(R.drawable.placeholder)
        else
            Glide.with(applicationContext)
                .load(track?.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornersInPx))
                .into(coverImageView)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}