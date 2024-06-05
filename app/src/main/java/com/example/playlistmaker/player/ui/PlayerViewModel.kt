package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val interactor: PlayerInteractor) : ViewModel() {
    companion object {
        const val DELAY_MILLIS = 300L
    }

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> = _elapsedTime

    private var timerJob: Job? = null

    init {
        _playerState.value = PlayerState.DEFAULT
    }

    fun preparePlayer(track: Track?) {
        interactor.preparePlayer(track,
            { _playerState.value = PlayerState.PREPARED },
            {
                _playerState.value = PlayerState.PREPARED
                _elapsedTime.value = CommonUtils.formatMillisToMmSs(0)
            })
    }

    private fun startPlayer() {
        interactor.startPlayer()
        _playerState.value = PlayerState.PLAYING
        startTimer()
    }

    fun pausePlayer() {
        interactor.pausePlayer()
        timerJob?.cancel()
        _playerState.value = PlayerState.PAUSED
    }

//    fun stopUpdateTimer() {
//        handler.removeCallbacks(updateTimerTask)
//    }

    fun playBackControl() {
        when (_playerState.value) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> {
                preparePlayer(_track.value)
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.PLAYING) {
                delay(DELAY_MILLIS)
                _elapsedTime.value = CommonUtils.formatMillisToMmSs(interactor.getCurrentPosition())
            }
        }
    }

//    private val updateTimerTask: Runnable = object : Runnable {
//        override fun run() {
//            if (_playerState.value == PlayerState.PLAYING) {
//                _elapsedTime.value = CommonUtils.formatMillisToMmSs(interactor.getCurrentPosition())
//                handler.postDelayed(this, DELAY_MILLIS)
//            }
//        }
//    }

    override fun onCleared() {
        interactor.release()
        super.onCleared()
    }
}