package com.example.playlistmaker.ui.activities.search

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.Creator
import com.example.playlistmaker.Keys
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.utils.SingleLiveEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getViewModelFactory(application: Application): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchViewModel(this[APPLICATION_KEY] as Application)
                }
            }
    }

    private val interactor: TracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(
        Keys.PLAYLIST_MAKER_PREFERENCES,
        AppCompatActivity.MODE_PRIVATE
    )

    private val _state = MutableLiveData<TracksState>()
    val state: LiveData<TracksState> = _state

    private val _toastState = SingleLiveEvent<String>()
    val toastState: LiveData<String> = _toastState

    private val _history = MutableLiveData<List<Track>>()
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
                Log.d("AAAA", "interactor tracks: " + tracks.toString())
                Log.d("AAAA", "interactor foundTracks: " + foundTracks.toString())

                when {
                    errorMessage != null -> {
                        renderState(
                            TracksState.Error(
                                errorMessage = getApplication<Application>().getString(R.string.download_failed)
                            )
                        )
                        showToast(errorMessage)
                    }

                    tracks.isEmpty() -> {
                        renderState(
                            TracksState.Empty(
                                message = getApplication<Application>().getString(R.string.nothing_found)
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

    fun saveToHistory(track: Track) {
        val historyList = getSearchHistory().toMutableList()
        historyList.removeAll { it.trackId == track.trackId }
        historyList.add(0, track)

        if (historyList.size > 10) {
            historyList.removeLast()
        }

        sharedPreferences.edit().putString(Keys.SEARCH_HISTORY_KEY, Gson().toJson(historyList)).apply()
        _history.value = historyList
    }

    fun getSearchHistory(): List<Track> {
        val history: MutableList<Track>? =
            Gson().fromJson(
                sharedPreferences.getString(Keys.SEARCH_HISTORY_KEY, null),
                object : TypeToken<List<Track>>() {}.type
            )
        return if (!history.isNullOrEmpty()) history else mutableListOf()
    }
    fun clearHistory() = {
        val emptyList: List<Track> = emptyList()
        sharedPreferences.edit().putString(Keys.SEARCH_HISTORY_KEY,
            Gson().toJson(emptyList)).apply()
        _history.value = emptyList
    }
}