package com.example.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.api.FavoritesInteractor
import com.example.playlistmaker.search.domain.models.Track

class FavoritesViewModel(favoritesInteractor: FavoritesInteractor): ViewModel() {
    private val _trackLiveData = MutableLiveData<Track>()
    val trackLiveData: LiveData<Track> = _trackLiveData
}