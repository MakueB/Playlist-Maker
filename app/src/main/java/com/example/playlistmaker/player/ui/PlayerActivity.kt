package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {


    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var binding: ActivityPlayerBinding

    private val args by navArgs<PlayerActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = args.track

        setFavoriteButton(track)

        binding.apply {
            country.text = track.country
            genre.text = track.primaryGenreName
            releaseDate.text = track.releaseDate?.substring(0, 4)

            if (track.collectionName.isNullOrEmpty()) {
                albumGroup.visibility = View.GONE
            } else {
                albumGroup.isVisible = true
                collectionName.text = track.collectionName
            }

            duration.text = track.trackDuration
            collectionNameTextView.text = track.collectionName
            trackName.text = track.trackName
        }

        val cornersInPx = CommonUtils.dpToPx(8f, this)

        binding.apply {
            if (track.artworkUrl100.isEmpty())
                cover.setImageResource(R.drawable.placeholder)
            else
                Glide.with(applicationContext)
                    .load(track.getCoverArtwork())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornersInPx))
                    .into(cover)

            backArrow.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.playButton.isEnabled = false

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }

        viewModel.playerState.observe(this) { state ->
            when (state) {
                PlayerState.PREPARED -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.setImageResource(R.drawable.button_play)
                }

                PlayerState.PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.button_pause)
                }

                PlayerState.PAUSED -> {
                    binding.playButton.setImageResource(R.drawable.button_play)
                }

                else -> {
                    // Обработка других состояний по необходимости
                }
            }
        }
        viewModel.elapsedTime.observe(this) { time ->
            binding.elapsedTime.text = time
        }
        viewModel.preparePlayer(track)

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.favorite_active)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
            }
        }
    }

    private fun setFavoriteButton(track: Track) {
        if (track.isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.favorite_active)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}