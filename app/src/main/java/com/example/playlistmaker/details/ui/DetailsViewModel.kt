package com.example.playlistmaker.details.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val playlistInteractor: PlaylistsInteractor,
    private val detailsInteractor: DetailsInteractor
    ) : ViewModel() {

    private val _shareCommand = SingleLiveEvent<ShareCommand>()
    val shareCommand: LiveData<ShareCommand> = _shareCommand

    fun onShareClicked(playlist: Playlist?) {
        _shareCommand.postValue(detailsInteractor.execute(playlist))
    }

    fun removeTrack(track: Track, playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(track, playlistId)
        }
    }
}