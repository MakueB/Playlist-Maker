package com.example.playlistmaker.ui.activities.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.utils.Keys
import com.example.playlistmaker.domain.api.OnTrackClickListener
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.activities.player.PlayerActivity


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val TEXT = "TEXT_DEF"
        private const val TEXT_DEF = ""
        const val TRACK_KEY = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text = TEXT_DEF
    private lateinit var binding: ActivitySearchBinding

    private val viewModel: SearchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory(this.application)
    }

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError(state.errorMessage)
            is TracksState.Empty -> showEmpty(state.message)
        }
    }

    private fun showLoading() {
        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.historyLinearLayout.isVisible = false
        binding.searchLinearLayout.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false
        binding.searchLinearLayout.isVisible = true

        Log.d("AAAA", tracks.toString())

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showError(errorMessage: String) {
        binding.searchLinearLayout.isVisible = true
        binding.searchPlaceholder.setImageResource(R.drawable.no_internet_light)
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.progressBar.isVisible = false
        showMessage(
            getString(R.string.connection_issues),
            getString(R.string.download_failed)
        )
    }

    private fun showEmpty(emptyMessage: String) {
        binding.searchLinearLayout.isVisible = true
        binding.searchPlaceholder.setImageResource(R.drawable.nothing_found_light)
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.historyLinearLayout.isVisible = false
        binding.progressBar.isVisible = false
        showMessage(getString(R.string.nothing_found), "")
    }

    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            binding.somethingWrongTexView.visibility = View.VISIBLE
            trackList.clear()
            adapter.notifyDataSetChanged()
            binding.somethingWrongTexView.text = text
            if (additionalText.isNotEmpty()) {
                binding.somethingWrongTexView.text = "$text\n\n$additionalText"
            }
        } else
            binding.somethingWrongTexView.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(Keys.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        val onTrackClickListener = OnTrackClickListener { track ->
            if (clickDebounce()) {
                val trackWasSavedMessage = getString(R.string.track_was_saved)
                val listContainsTrack = historyList.any { it.trackId == track.trackId }

                viewModel.saveToHistory(track)
                viewModel.saveToHistory(track)
                if (!listContainsTrack)
                    Toast.makeText(this@SearchActivity, trackWasSavedMessage, Toast.LENGTH_SHORT)
                        .show()

                val playerActivityIntent = Intent(this, PlayerActivity::class.java)
                playerActivityIntent.putExtra(TRACK_KEY, track)
                startActivity(playerActivityIntent)
            }
        }

        adapter = TrackAdapter(onTrackClickListener)
        historyAdapter = TrackAdapter(onTrackClickListener)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.tracks = trackList
        binding.recyclerView.adapter = adapter
        historyAdapter.tracks = historyList
        binding.historyRecyclerView.adapter = historyAdapter

        showHistory()

        if (historyList.isNotEmpty()) {
            binding.historyLinearLayout.visibility = View.VISIBLE
            binding.searchLinearLayout.visibility = View.GONE
        } else {
            binding.historyLinearLayout.visibility = View.GONE
            binding.searchLinearLayout.visibility = View.VISIBLE
        }

        binding.editText.requestFocus()
        showKeyboard(binding.editText)


        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && (binding.editText.text?.isBlank() == true)) {
                binding.historyLinearLayout.isVisible = true
                binding.searchLinearLayout.isVisible = false
                showHistory()
            } else {
                binding.historyLinearLayout.isVisible = false
                binding.searchLinearLayout.isVisible = true
            }
        }


        binding.imageViewClear.setOnClickListener {
            binding.editText.setText("")
            trackList.clear()
            adapter.notifyDataSetChanged()
        }

        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.search(binding.editText.text.toString())
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter.notifyDataSetChanged()
            binding.historyLinearLayout.isVisible = false
            binding.searchLinearLayout.isVisible = true
        }

        binding.editText.addTextChangedListener(
            onTextChanged = { s, _, _, _ -> //s - charSequence
                binding.imageViewClear.isVisible = !s.isNullOrEmpty()
                checkAndHideKeyboard(binding.editText)

                if (s?.length ?: -1 > 1) //не начинать поиск при пустой строке ввода
                    viewModel.searchDebounce(s.toString() ?: "")

                if (binding.editText.hasFocus() && s?.isBlank() == true) {
                    binding.historyLinearLayout.isVisible = true
                    binding.searchLinearLayout.isVisible = false
                    showHistory()
                } else {
                    binding.historyLinearLayout.isVisible = false
                    binding.searchLinearLayout.isVisible = true
                }
            }
        )

        viewModel.state.observe(this) { state ->
            render(state)
        }
        viewModel.toastState.observe(this) {
            showToast(it)
        }
        viewModel.history.observe(this) {
            historyAdapter.tracks = it.toMutableList()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showHistory() {
        historyList.clear()
        historyList.addAll(viewModel.getSearchHistory())
        adapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT, text)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT, TEXT_DEF)
        binding.editText.setText(text)
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}







