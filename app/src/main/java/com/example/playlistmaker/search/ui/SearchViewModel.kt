package com.example.playlistmaker.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(private val interactor: TracksInteractor) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _state = MutableLiveData<TracksState>()
    val state: LiveData<TracksState> = _state

    private val _toastState = SingleLiveEvent<String>()
    val toastState: LiveData<String> = _toastState

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history = _history.asStateFlow()

    private var lastTextSearch: String? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            search(changedText)
        }

    init {
        viewModelScope.launch {
            updateSearchHistory()
        }
    }

    private fun showToast(message: String) {
        _toastState.postValue(message)
    }

    fun search(query: String) {
        if (query.isNotEmpty()) {

            renderState(TracksState.Loading)

            viewModelScope.launch {
                interactor
                    .search(query)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()

        foundTracks?.let {
            tracks.addAll(it)
        }

        when {
            errorMessage != null -> {
                renderState(
                    TracksState.Error(
                        errorMessage = R.string.download_failed
                    )
                )
                showToast(errorMessage)
            }

            tracks.isEmpty() -> {
                renderState(
                    TracksState.Empty(
                        message = R.string.nothing_found
                    )
                )
            }

            else -> {
                renderState(
                    TracksState.Content(
                        tracks = tracks
                    )
                )
            }
        }
    }

    fun searchDebounce(text: String) {
        if (lastTextSearch != text) {
            lastTextSearch = text
            trackSearchDebounce(text)
        }
    }

    private fun renderState(state: TracksState) {
        _state.postValue(state)
    }

    fun saveToHistory(track: Track) {
        viewModelScope.launch (Dispatchers.IO) {
                interactor.saveToHistory(track)
                updateSearchHistory()
        }
    }

    fun updateSearchHistory()  {
        viewModelScope.launch (Dispatchers.IO) {
            val list = interactor.getSearchHistory()
            _history.value = list
        }
    }

    fun updateSearchHistoryAsync() : Deferred<Unit> {
        return viewModelScope.async (Dispatchers.IO) {
            val list = interactor.getSearchHistory()
            _history.value = list
        }
    }

    fun clearHistory() {
        viewModelScope.launch (Dispatchers.IO) {
            interactor.clearHistory()
            updateSearchHistory()
        }
    }
}