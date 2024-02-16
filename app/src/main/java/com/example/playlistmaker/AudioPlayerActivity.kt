package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.CommonUtils.parcelable
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.parcelable<Track>(SearchActivity.TRACK_KEY)
        binding.apply {
            country.text = track?.country
            genre.text = track?.primaryGenreName
            releaseDate.text = track?.releaseDate?.substring(0, 4)

            if (track?.collectionName.isNullOrEmpty()) {
                albumGroup.visibility = View.GONE
            } else {
                albumGroup.isVisible = true
                collectionName.text = track?.collectionName
            }

            duration.text = CommonUtils.convert(track?.trackTimeMillis ?: 0)
            collectionNameTextView.text = track?.collectionName
            trackName.text = track?.trackName
        }

        val cornersInPx = CommonUtils.dpToPx(2f, this)

        binding.apply {
            if (track?.artworkUrl100.isNullOrEmpty())
                cover.setImageResource(R.drawable.placeholder)
            else
                Glide.with(applicationContext)
                    .load(track?.getCoverArtwork())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornersInPx))
                    .into(cover)

            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}