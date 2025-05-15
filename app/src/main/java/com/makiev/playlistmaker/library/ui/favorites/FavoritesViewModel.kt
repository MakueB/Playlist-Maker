package com.makiev.playlistmaker.library.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.library.domain.favorites.api.FavoritesInteractor
import com.makiev.playlistmaker.search.domain.api.TracksInteractor
import com.makiev.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val tracksInteractor: TracksInteractor,
    private val application: Application
): AndroidViewModel(application) {
    private val _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favoritesState

    fun getFavorites() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor.getFavoritesAll().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty(application.getString(R.string.library_is_empty)))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        _favoritesState.postValue(state)
    }

    fun saveToHistory(track: Track) {
        viewModelScope.launch (Dispatchers.IO) {
            tracksInteractor.saveToHistory(track)
        }
    }
}