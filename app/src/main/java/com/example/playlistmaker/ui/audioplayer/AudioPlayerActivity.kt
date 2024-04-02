package com.example.playlistmaker.ui.audioplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.utils.CommonUtils.parcelable
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        const val DELAY_MILLIS = 1000L
    }

    private var playerState = AudioPlayerState.DEFAULT
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer = MediaPlayer()

    private fun preparePlayer(track: Track?) {
        mediaPlayer.apply {
            setDataSource(track?.previewUrl)
            prepareAsync()
            setOnPreparedListener{
                binding.playButton.isEnabled = true
                playerState = AudioPlayerState.PREPARED
            }
            setOnCompletionListener {
                binding.playButton.setImageResource(R.drawable.button_play)
                playerState = AudioPlayerState.PREPARED
                binding.elapsedTime.text = getString(R.string.timer_default_value)
            }
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.button_pause)
        playerState = AudioPlayerState.PLAYING
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.button_play)
        playerState = AudioPlayerState.PAUSED
    }

    private fun playBackControl(){
        when (playerState) {
            AudioPlayerState.PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(updateTimerTask)
            }
            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> {
                startPlayer()
                handler.postDelayed(updateTimerTask, DELAY_MILLIS)
            }
        }
    }

    private val updateTimerTask: Runnable = object : Runnable {
        override fun run() {
            if (playerState == AudioPlayerState.PLAYING){
                binding.elapsedTime.text = CommonUtils.formatMillisToMmSs(mediaPlayer.currentPosition.toLong())
                handler.post(this)
            }
        }
    }


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

            duration.text = CommonUtils.formatMillisToMmSs(track?.trackTimeMillis ?: 0)
            collectionNameTextView.text = track?.collectionName
            trackName.text = track?.trackName
        }

        val cornersInPx = CommonUtils.dpToPx(8f, this)

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

        binding.playButton.isEnabled = false

        preparePlayer(track)

        binding.playButton.setOnClickListener {
            playBackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateTimerTask)
    }
}