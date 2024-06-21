package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    companion object {
        const val DELAY_MILLIS = 300L
    }

    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> = _track

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> = _elapsedTime

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var timerJob: Job? = null

    init {
        _playerState.value = PlayerState.DEFAULT
    }

    fun preparePlayer(track: Track?) {
        playerInteractor.preparePlayer(track,
            { _playerState.value = PlayerState.PREPARED },
            {
                timerJob?.cancel()
                _playerState.value = PlayerState.PREPARED
                _elapsedTime.value = CommonUtils.formatMillisToMmSs(0)
            })
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.value = PlayerState.PLAYING
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        _playerState.value = PlayerState.PAUSED
    }

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
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.PLAYING) {
                delay(DELAY_MILLIS)
                _elapsedTime.value =
                    CommonUtils.formatMillisToMmSs(playerInteractor.getCurrentPosition())
            }
        }
    }

    fun onFavoriteClicked(track: Track) {
        when (track.isFavorite) {
            true -> viewModelScope.launch (Dispatchers.IO) {
                track.isFavorite = false
                favoritesInteractor.removeFromFavorites(track)
                _isFavorite.postValue(false)
            }

            false -> viewModelScope.launch (Dispatchers.IO) {
                track.isFavorite = true
                favoritesInteractor.addToFavorites(track)
                _isFavorite.postValue(true)
            }
        }
    }

    override fun onCleared() {
        playerInteractor.release()
        super.onCleared()
    }
}