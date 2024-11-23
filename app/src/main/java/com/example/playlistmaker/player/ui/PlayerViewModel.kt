package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.favorites.api.FavoritesInteractor
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.library.ui.playlists.PlaylistsState
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
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

    private val _state = MutableLiveData<PlaylistsState>()
    val state get() = _state

    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistLiveData: LiveData<List<Playlist>> = _playlistsLiveData

    private var timerJob: Job? = null

    init {
        _playerState.value = PlayerState.DEFAULT
    }

    private fun renderState(state: PlaylistsState) {
        _state.postValue(state)
    }

    fun getPlaylistsAll() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            playlistsInteractor.getPlaylistsAll().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty(R.string.no_playlists.toString()))
        } else {
            renderState(PlaylistsState.Content(playlists))
            _playlistsLiveData.postValue(playlists)
        }
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

    fun setTrack(track: Track) {
        _track.value = track
    }

    fun release() {
        playerInteractor.release()
    }

    fun reset() {
        playerInteractor.reset()
    }

    override fun onCleared() {
        playerInteractor.release()
        super.onCleared()
    }
}