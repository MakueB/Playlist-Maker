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
import android.view.View
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
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

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
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.parcelable<Track>(Keys.TRACK_KEY)
        binding.apply {
            country.setText(track?.country) ?: ""
            genre.setText(track?.primaryGenreName) ?: ""
            releaseDate.setText(track?.releaseDate?.substring(0, 4)) ?: ""

            if (track?.collectionName.isNullOrEmpty()) {
                albumGroup.visibility = View.GONE
            } else {
                albumGroup.isVisible = true
                collectionName.setText(track?.collectionName) ?: ""
            }

            duration.setText(convert(track?.trackTimeMillis ?: 0))
            collectionNameTextView.setText(track?.collectionName) ?: ""
            trackName.setText(track?.trackName) ?: ""
        }

        val cornersInPx = dpToPx(2f, this)

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