package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var youVeBeenSearchingMessage:TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    //val app = application as App

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()

    private fun addTrackToHistoryList(track: Track){
        val trackId = track.trackId
        if (historyList.any { it.trackId == trackId }){
            historyList.removeIf { it.trackId == trackId }
            historyList.add(0,track)
            historyAdapter.notifyDataSetChanged()
        } else {
            historyList.add(0,track)
            historyAdapter.notifyDataSetChanged()
        }
        if (historyList.size > 10)
            historyList.remove(historyList.lastOrNull())
    }


    private fun search() {
        val call = iTunesService.search(editText.text.toString()).enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: retrofit2.Response<ITunesResponse>
            ) {
                if (response.code() == resources.getInteger(R.integer.itunes_response_code_success)){
                    Log.d("y", response.body()?.results.toString())
                    if (response.body()?.results?.isNotEmpty() == true){
                        trackList.clear()
                        trackList.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        setPlaceholders(SearchStatus.SUCCESS)
                    } else {
                        setPlaceholders(SearchStatus.NOTHING_FOUND)
                    }
                } else {
                    setPlaceholders(SearchStatus.FAILURE)
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                setPlaceholders(SearchStatus.FAILURE)
            }
        })
    }



    private fun setPlaceholders(status: SearchStatus){
        when (status){
            SearchStatus.SUCCESS -> {
                placeHolderImage.isVisible = false
                smthWrongMessage.isVisible = false
                refreshButton.isVisible = false
            }
            SearchStatus.NOTHING_FOUND -> {
                placeHolderImage.setImageResource(R.drawable.nothing_found_light)
                placeHolderImage.visibility = View.VISIBLE
                showMessage(getString(R.string.nothing_found),"")
            }
            SearchStatus.FAILURE -> {
                placeHolderImage.setImageResource(R.drawable.no_internet_light)
                placeHolderImage.visibility = View.VISIBLE
                showMessage(getString(R.string.connection_issues),
                    getString(R.string.download_failed))
            }
        }
    }

    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            smthWrongMessage.visibility = View.VISIBLE
            trackList.clear()
            adapter.notifyDataSetChanged()
            smthWrongMessage.text = text
            if (additionalText.isNotEmpty()){
                smthWrongMessage.text = "$text\n\n$additionalText"
            }
        } else
            smthWrongMessage.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)



        val sharedPreferences = getSharedPreferences(Keys.PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val onTrackClickListener = object : OnTrackClickListener {
            override fun onTrackClick(track: Track) {
                addTrackToHistoryList(track)
                searchHistory.saveToPrefs(historyList)
                Toast.makeText(this@SearchActivity, "Трек сохранен", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = TrackAdapter(onTrackClickListener)
        historyAdapter = TrackAdapter(onTrackClickListener)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.tracks = trackList
        recyclerView.adapter = adapter
        historyAdapter.tracks = historyList
        historyRecyclerView.adapter = historyAdapter

        val linearLayout = findViewById<LinearLayout>(R.id.searchContainer)
        editText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.imageViewClear)
        val backButton = findViewById<ImageView>(R.id.backArrow)
        placeHolderImage = findViewById<ImageView>(R.id.searchPlaceholder)
        smthWrongMessage = findViewById(R.id.somethingWrongTexView)
        refreshButton = findViewById(R.id.refreshButton)
        searchLinearLayout = findViewById(R.id.searchLinearLayout)
        historyLinearLayout = findViewById(R.id.historyLinearLayout)
        youVeBeenSearchingMessage = findViewById(R.id.youVeBeenSearching)
        clearHistoryButton = findViewById(R.id.clearHistory)

        showHistory()

        if (!historyList.isNullOrEmpty()) {
            historyLinearLayout.visibility = View.VISIBLE
            searchLinearLayout.visibility = View.GONE
        } else {
            historyLinearLayout.visibility = View.GONE
            searchLinearLayout.visibility = View.VISIBLE
        }

        editText.requestFocus()
        showKeyboard(editText)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        
        editText.setOnFocusChangeListener { viev, hasFocus ->
            if (hasFocus && editText.text.isBlank()){
                historyLinearLayout.visibility = View.VISIBLE
                searchLinearLayout.visibility = View.GONE
                showHistory()
            } else {
                historyLinearLayout.visibility = View.GONE
                searchLinearLayout.visibility = View.VISIBLE
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
            historyLinearLayout.visibility = View.GONE
            searchLinearLayout.visibility = View.VISIBLE
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                checkAndHideKeyboard(editText)
                text = s.toString()

                if (editText.hasFocus() && s?.isBlank() == true){
                    historyLinearLayout.visibility = View.VISIBLE
                    searchLinearLayout.visibility = View.GONE
                    showHistory()
                } else {
                    historyLinearLayout.visibility = View.GONE
                    searchLinearLayout.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        editText.addTextChangedListener(textWatcher)
    }

    private fun showHistory(){
        historyList.clear()
        historyList.addAll(searchHistory.loadFromPrefs())
        adapter.notifyDataSetChanged()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT, text)
        Log.d("my", "onSaveInstanceState: Saved text - $text")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT, TEXT_DEF)
        editText.setText(text)
        Log.d("my", "onRestoreInstanceState: Restored text - $text")
    }


    companion object {
        const val TEXT = "TEXT_DEF"
        const val TEXT_DEF = ""
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun hideKeyboard (editText: EditText) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }
    private fun checkAndHideKeyboard(editText: EditText) {
        val text = editText.text.toString().trim()
        // Если текст пуст, скрываем клавиатуру
        if (text.isNullOrEmpty()) {
            hideKeyboard(editText)
        }
        else {
            showKeyboard(editText)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}







