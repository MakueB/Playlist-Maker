package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.Playlist

class PlaylistsViewModel(private val playlist: Playlist): ViewModel()  {
    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> = _playlistLiveData
}