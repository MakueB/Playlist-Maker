package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.CommonUtils.parcelable
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_MILLIS = 1000L
    }

    private var playerState = STATE_DEFAULT
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer = MediaPlayer()

    private fun preparePlayer(track: Track?) {
        mediaPlayer.apply {
            setDataSource(track?.previewUrl)
            prepareAsync()
            setOnPreparedListener{
                binding.playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                binding.playButton.setImageResource(R.drawable.button_play)
                playerState = STATE_PREPARED
                binding.elapsedTime.text = getString(R.string.timer_default_value)
            }
        }
    }

    private fun startPlayer(){
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer(){
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED
    }

    private fun playBackControl(){
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(updateTimerTask)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                handler.postDelayed(updateTimerTask, DELAY_MILLIS)
            }
        }
    }

    private val updateTimerTask: Runnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING){
                binding.elapsedTime.text = CommonUtils.convert(mediaPlayer.currentPosition.toLong())
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

            duration.text = CommonUtils.convert(track?.trackTimeMillis ?: 0)
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
//            Log.d("Check", "is playing: " +
//                    mediaPlayer.isPlaying.toString()
//                    + " playerState: " + playerState)
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