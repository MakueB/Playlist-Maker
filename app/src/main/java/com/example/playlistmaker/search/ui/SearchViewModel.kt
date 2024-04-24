package com.example.playlistmaker.search.ui

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(private val interactor: TracksInteractor, private val context: Context) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val _state = MutableLiveData<TracksState>()
    val state: LiveData<TracksState> = _state

    private val _toastState = SingleLiveEvent<String>()
    val toastState: LiveData<String> = _toastState

    private val _history = MutableLiveData<List<Track>>().apply { interactor.getSearchHistory() }
    val history: LiveData<List<Track>> = _history

    private var lastTextSearch: String? = null

    fun showToast(message: String) {
        _toastState.postValue(message)
    }

    fun search(query: String) {
        interactor.search(query, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = mutableListOf<Track>()

                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }

                when {
                    errorMessage != null -> {
                        renderState(
                            TracksState.Error(
                                errorMessage = context.getString(R.string.download_failed)
                            )
                        )
                        showToast(errorMessage)
                    }

                    tracks.isEmpty() -> {
                        renderState(
                            TracksState.Empty(
                                message = context.getString(R.string.nothing_found)
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
        })
    }

    fun searchDebounce(text: String) {
        if (lastTextSearch == text)
            return

        this.lastTextSearch = text
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(text) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    private fun renderState(state: TracksState) {
        _state.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun removeCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun saveToHistory(track: Track) {
        interactor.saveToHistory(track)
        updateSearchHistory()
    }

    fun updateSearchHistory() {
        val list = interactor.getSearchHistory()
        _history.value = list
    }

    fun clearHistory() {
        interactor.clearHistory()
        updateSearchHistory()
    }
}