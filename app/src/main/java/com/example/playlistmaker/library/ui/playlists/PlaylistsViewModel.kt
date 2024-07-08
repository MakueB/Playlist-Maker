package com.example.playlistmaker.library.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.newplaylist.domain.models.Playlist

class PlaylistsViewModel(private val playlist: Int): ViewModel()  {
    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> = _playlistLiveData
}