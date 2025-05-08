package com.example.playlistmaker.details.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val playlistInteractor: PlaylistsInteractor,
    private val detailsInteractor: DetailsInteractor,
    private val mapper: PlaylistMapper
) : ViewModel() {

    private val _shareCommand = SingleLiveEvent<ShareCommand>()
    val shareCommand: LiveData<ShareCommand> = _shareCommand

    private val _state = MutableLiveData<PlaylistUiModel>()
    val state: LiveData<PlaylistUiModel> = _state

    private var playlistId: Long = -1L

    fun loadPlaylist(id: Long) {
        playlistId = id
        viewModelScope.launch {
            playlistInteractor.getPlaylistsAll().collect { playlists ->
                val playlist = playlists.find { it.id == id } ?: return@collect
                _state.value = mapper.toUiModel(playlist)
            }
        }
    }

    fun updatePlaylist(updatedPlaylist: Playlist) {
        _state.postValue(mapper.toUiModel(updatedPlaylist))
    }

    fun onShareClicked() {
        val playlist = _state.value ?: return
        val command = detailsInteractor.getShareCommand(playlist)
        _shareCommand.postValue(command)
    }

    fun removeTrack(track: Track) {
        val current = _state.value ?: return

        val updatedTracks = current.tracks.toMutableList().apply {
            removeIf { it.trackId == track.trackId }
        }

        val newCount = updatedTracks.size
        val durationMinutes = CommonUtils.getTotalDurationInMinutes(updatedTracks)
        val formattedDuration = CommonUtils.formatMinutesText(durationMinutes)

        val updatedState = current.copy(
            tracks = updatedTracks,
            trackCount = newCount,
            totalDuration = formattedDuration,
            trackWordForm = CommonUtils.getTrackWordForm(newCount)
        )

        _state.value = updatedState

        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(track, current.id)

            updatePlaylist(
                Playlist(
                    id = current.id,
                    name = current.name,
                    description = current.description ?: "",
                    imageUrl = current.imageUrl,
                    trackList = updatedTracks
                )
            )
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }
}