package com.example.playlistmaker.ui.activities.player

import PlayerState
import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils

class PlayerViewModel (private val mediaPlayer: MediaPlayer) : ViewModel() {
    companion object {
        const val DELAY_MILLIS = 500L
        fun getViewModelFactory(mediaPlayer: MediaPlayer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(mediaPlayer = mediaPlayer)
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> = _elapsedTime

    init {
        _playerState.value = PlayerState.DEFAULT
    }
    fun preparePlayer(track: Track?) {
        mediaPlayer.apply {
            setDataSource(track?.previewUrl)
            prepareAsync()
            setOnPreparedListener{
                _playerState.value = PlayerState.PREPARED
            }
            setOnCompletionListener {
                _playerState.value = PlayerState.PREPARED
                _elapsedTime.value = CommonUtils.formatMillisToMmSs(0)
            }
        }
    }

    fun startPlayer(){
        mediaPlayer.start()
        _playerState.value = PlayerState.PLAYING
    }

    fun pausePlayer(){
        mediaPlayer.pause()
        _playerState.value = PlayerState.PAUSED
    }

    fun stopUpdateTimer(){
        handler.removeCallbacks(updateTimerTask)
    }

    fun playBackControl(){
        when (_playerState.value) {
            PlayerState.PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(updateTimerTask)
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
                handler.postDelayed(updateTimerTask, DELAY_MILLIS)
            }
            else -> {
                preparePlayer(_track.value)
            }
        }
    }

    private val updateTimerTask: Runnable = object : Runnable {
        override fun run() {
            if (_playerState.value == PlayerState.PLAYING){
                _elapsedTime.value = CommonUtils.formatMillisToMmSs(mediaPlayer.currentPosition.toLong())
                handler.postDelayed(this, DELAY_MILLIS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}