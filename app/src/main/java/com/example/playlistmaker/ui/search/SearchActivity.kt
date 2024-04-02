package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.ITunesResponse
import com.example.playlistmaker.Keys
import com.example.playlistmaker.OnTrackClickListener
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.SearchStatus
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioplayer.AudioPlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class SearchActivity : AppCompatActivity() {
    private var text = TEXT_DEF
    private lateinit var editText: EditText
    private lateinit var placeHolderImage: ImageView
    private lateinit var smthWrongMessage: TextView
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchLinearLayout: LinearLayout
    private lateinit var historyLinearLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var youVeBeenSearchingMessage: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()

    private fun addTrackToHistoryList(track: Track) {
        val trackId = track.trackId

        historyList.removeIf { it.trackId == trackId }
        historyList.add(0, track)

        if (historyList.size > 10)
            historyList.remove(historyList.lastOrNull())

        historyAdapter.notifyDataSetChanged()
    }


    private fun search() {
        setPlaceholders(SearchStatus.SEARCHING)

        iTunesService.search(editText.text.toString()).enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: retrofit2.Response<ITunesResponse>
            ) {
                if (response.code() == resources.getInteger(R.integer.itunes_response_code_success)) {
                    // #no-commit             Log.d("body", response.body()?.results.toString() +
                    // #no-commit "code " + response.code())
                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.clear()
                        trackList.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        setPlaceholders(SearchStatus.SUCCESS)
                    } else {
// #no-commit                       Log.d("body", response.body()?.results.toString()
// #no-commit                       + "code " + response.code())
                        setPlaceholders(SearchStatus.NOTHING_FOUND)
                    }
                } else {
                    setPlaceholders(SearchStatus.FAILURE)
// #no-commit                   Log.d("body", response.body()?.results.toString()
// #no-commit                            + "code " + response.code())
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                //Log.d("body", t.message.toString())
                setPlaceholders(SearchStatus.FAILURE)
            }
        })
    }

    private fun setPlaceholders(status: SearchStatus) {
        when (status) {
            SearchStatus.SUCCESS -> {
                placeHolderImage.isVisible = false
                smthWrongMessage.isVisible = false
                refreshButton.isVisible = false
                progressBar.isVisible = false
                searchLinearLayout.isVisible = true
            }

            SearchStatus.SEARCHING -> {
                placeHolderImage.isVisible = false
                smthWrongMessage.isVisible = false
                refreshButton.isVisible = false
                historyLinearLayout.isVisible = false
                searchLinearLayout.isVisible = false
                progressBar.isVisible = true
            }

            SearchStatus.NOTHING_FOUND -> {
                searchLinearLayout.isVisible = true
                placeHolderImage.setImageResource(R.drawable.nothing_found_light)
                placeHolderImage.visibility = View.VISIBLE
                progressBar.isVisible = false
                showMessage(getString(R.string.nothing_found), "")
            }

            SearchStatus.FAILURE -> {
                searchLinearLayout.isVisible = true
                placeHolderImage.setImageResource(R.drawable.no_internet_light)
                placeHolderImage.visibility = View.VISIBLE
                progressBar.isVisible = false
                showMessage(
                    getString(R.string.connection_issues),
                    getString(R.string.download_failed)
                )
            }
        }
    }

    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            smthWrongMessage.visibility = View.VISIBLE
            trackList.clear()
            adapter.notifyDataSetChanged()
            smthWrongMessage.text = text
            if (additionalText.isNotEmpty()) {
                smthWrongMessage.text = "$text\n\n$additionalText"
            }
        } else
            smthWrongMessage.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)


        val sharedPreferences = getSharedPreferences(Keys.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val onTrackClickListener = OnTrackClickListener { track ->
            if (clickDebounce()) {
                val trackWasSavedMessage = getString(R.string.track_was_saved)
                val listContainsTrack = historyList.any { it.trackId == track.trackId }

                addTrackToHistoryList(track)
                searchHistory.saveToPrefs(historyList)
                if (!listContainsTrack)
                    Toast.makeText(this@SearchActivity, trackWasSavedMessage, Toast.LENGTH_SHORT)
                        .show()

                val audioPlayerActivityIntent = Intent(this, AudioPlayerActivity::class.java)
                audioPlayerActivityIntent.putExtra(TRACK_KEY, track)
                startActivity(audioPlayerActivityIntent)
            }
        }

        adapter = TrackAdapter(onTrackClickListener)
        historyAdapter = TrackAdapter(onTrackClickListener)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.tracks = trackList
        recyclerView.adapter = adapter
        historyAdapter.tracks = historyList
        historyRecyclerView.adapter = historyAdapter

        editText = findViewById(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.imageViewClear)
        val backButton = findViewById<ImageView>(R.id.backArrow)
        placeHolderImage = findViewById(R.id.searchPlaceholder)
        smthWrongMessage = findViewById(R.id.somethingWrongTexView)
        refreshButton = findViewById(R.id.refreshButton)
        searchLinearLayout = findViewById(R.id.searchLinearLayout)
        historyLinearLayout = findViewById(R.id.historyLinearLayout)
        youVeBeenSearchingMessage = findViewById(R.id.youVeBeenSearching)
        clearHistoryButton = findViewById(R.id.clearHistory)
        progressBar = findViewById(R.id.progressBar)

        showHistory()

        if (historyList.isNotEmpty()) {
            historyLinearLayout.visibility = View.VISIBLE
            searchLinearLayout.visibility = View.GONE
        } else {
            historyLinearLayout.visibility = View.GONE
            searchLinearLayout.visibility = View.VISIBLE
        }

        editText.requestFocus()
        showKeyboard(editText)


        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText.text.isBlank()) {
                historyLinearLayout.isVisible = true
                searchLinearLayout.isVisible = false
                showHistory()
            } else {
                historyLinearLayout.isVisible = false
                searchLinearLayout.isVisible = true
            }
        }


        clearButton.setOnClickListener {
            editText.setText("")
            trackList.clear()
            adapter.notifyDataSetChanged()
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        refreshButton.setOnClickListener {
            search()
        }

        clearHistoryButton.setOnClickListener {
            historyList.clear()
            searchHistory.saveToPrefs(historyList)
            historyAdapter.notifyDataSetChanged()
            historyLinearLayout.isVisible = false
            searchLinearLayout.isVisible = true
        }

        editText.addTextChangedListener(
            onTextChanged = {s, _, _, _ -> //s - charSequence
                clearButton.isVisible = !s.isNullOrEmpty()
                checkAndHideKeyboard(editText)
                text = s.toString()
                searchDebounce()

                if (editText.hasFocus() && s?.isBlank() == true) {
                    historyLinearLayout.isVisible = true
                    searchLinearLayout.isVisible = false
                    showHistory()
                } else {
                    historyLinearLayout.isVisible = false
                    searchLinearLayout.isVisible = true
                }
            }
        )
    }

    private fun showHistory() {
        historyList.clear()
        historyList.addAll(searchHistory.loadFromPrefs())
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT, text)
        //Log.d("my", "onSaveInstanceState: Saved text - $text")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT, TEXT_DEF)
        editText.setText(text)
//        Log.d("my", "onRestoreInstanceState: Restored text - $text")
    }


    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun checkAndHideKeyboard(editText: EditText) {
        val text = editText.text.toString().trim()
        // Если текст пуст, скрываем клавиатуру
        if (text.isEmpty()) {
            hideKeyboard(editText)
        } else {
            showKeyboard(editText)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed( {isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce(){
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        const val TEXT = "TEXT_DEF"
        const val TEXT_DEF = ""
        const val TRACK_KEY = "track"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}







