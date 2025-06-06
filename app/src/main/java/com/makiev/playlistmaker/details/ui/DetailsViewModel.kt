package com.makiev.playlistmaker.details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makiev.playlistmaker.details.domain.api.DetailsInteractor
import com.makiev.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.Utils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val playlistInteractor: PlaylistsInteractor,
    private val detailsInteractor: DetailsInteractor,
    private val mapper: PlaylistMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DetailsUiEffect>()
    val uiEffect: SharedFlow<DetailsUiEffect> = _uiEffect.asSharedFlow()

    private var playlistId: Long = -1L

    fun onEvent(event: DetailsUiEvent) {
        when (event) {
            is DetailsUiEvent.LoadPlaylist -> loadPlaylist(event.id)
            is DetailsUiEvent.RemoveTrack -> removeTrack(event.track)
            is DetailsUiEvent.DeletePlaylist -> deletePlaylist()
            is DetailsUiEvent.ShareClicked -> handleShare()
        }
    }

    private fun loadPlaylist(id: Long) {
        playlistId = id
        viewModelScope.launch {
            playlistInteractor.getPlaylistsAll().collect { playlists ->
                playlists.find { it.id == id }?.let {
                    _uiState.value = DetailsUiState.Content(mapper.toUiModel(it))
                }
            }
        }
    }

    private fun handleShare() {
        val state = _uiState.value
        if (state !is DetailsUiState.Content) return

        val command = detailsInteractor.getShareCommand(state.playlist)
        viewModelScope.launch {
            when (command) {
                is ShareCommand.SharePlaylist -> {
                    _uiEffect.emit(DetailsUiEffect.Share(command.text))
                }

                is ShareCommand.ShowEmptyPlaylistMessage -> {
                    _uiEffect.emit(DetailsUiEffect.ShowEmptyPlaylistToast)
                }
            }
        }
    }

    fun removeTrack(track: Track) {
        val state = _uiState.value
        if (state !is DetailsUiState.Content) return

        val currentPlaylist = state.playlist
        val updatedTracks = currentPlaylist.tracks.filter { it.trackId != track.trackId }

        val newCount = updatedTracks.size
        val durationMinutes = Utils.getTotalDurationInMinutes(updatedTracks)
        val formattedDuration = Utils.formatMinutesText(durationMinutes)

        val updatedPlaylist = currentPlaylist.copy(
            trackCount = newCount,
            totalDuration = formattedDuration,
            tracks = updatedTracks,
            trackWordForm = Utils.getTrackWordForm(newCount)
        )
        _uiState.value = DetailsUiState.Content(updatedPlaylist)

        viewModelScope.launch {
            detailsInteractor.removeTrackFromPlaylist(track.trackId, currentPlaylist.id)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }
}