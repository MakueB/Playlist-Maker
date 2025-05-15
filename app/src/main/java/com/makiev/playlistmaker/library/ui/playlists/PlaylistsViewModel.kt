package com.makiev.playlistmaker.library.ui.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
): ViewModel()  {
    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()

    private val _state = MutableLiveData<PlaylistsState>()
    val state get() = _state

    private fun renderState(state: PlaylistsState) {
        _state.postValue(state)
    }

    fun getPlaylistsAll() {
        renderState(PlaylistsState.Loading)
        viewModelScope.launch {
            interactor.getPlaylistsAll().collect { playlists ->
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

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.deletePlaylist(playlist.id)
            getPlaylistsAll()
        }
    }
}