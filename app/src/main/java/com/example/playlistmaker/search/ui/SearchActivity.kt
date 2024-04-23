package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity


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
        SearchViewModel.getViewModelFactory()
    }

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val trackList = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onTrackClickListener =  { track: Track ->
            if (clickDebounce()) {
                val trackWasSavedMessage = getString(R.string.track_was_saved)
                val listContainsTrack =
                    viewModel.history.value?.any { it.trackId == track.trackId } ?: false

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

        viewModel.updateSearchHistory()
        historyAdapter.tracks = viewModel.history.value?.toMutableList() ?: mutableListOf()
        binding.historyRecyclerView.adapter = historyAdapter

        binding.editText.requestFocus()
        showKeyboard(binding.editText)

        setupListeners()
        setupObservers()

        if (!viewModel.history.value.isNullOrEmpty()) {
            render(TracksState.History(viewModel.history.value ?: emptyList()))
        }
        else {
            render(TracksState.Content(trackList))
        }
    }

    private fun setupObservers() {
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
    private fun setupListeners() {
        binding.editText.addTextChangedListener(
            onTextChanged = { s, _, _, _ -> //s - charSequence
                binding.imageViewClear.isVisible = !s.isNullOrEmpty()
                //checkAndHideKeyboard(binding.editText)
                text = s.toString()

                if ((s?.length ?: -1) > 1) {//не начинать поиск при пустой строке ввода
                    viewModel.searchDebounce(s.toString())

                    if ((binding.editText.hasFocus()
                                && s?.isBlank() == true) && !viewModel.history.value.isNullOrEmpty()
                    ) {
                        viewModel.removeCallbacks()
                        render(TracksState.History(viewModel.history.value ?: emptyList()))
                    } else {
                        render(TracksState.Content(trackList))
                    }
                } else {
                    viewModel.removeCallbacks()
                    if (!viewModel.history.value.isNullOrEmpty())
                        render(TracksState.History(viewModel.history.value ?: emptyList()))
                    else
                        render(TracksState.Content(trackList))
                }
            }
        )
        binding.imageViewClear.setOnClickListener {
            binding.editText.setText("")
            trackList.clear()
            adapter.notifyDataSetChanged()

            if (!viewModel.history.value.isNullOrEmpty()) {
                render(TracksState.History(viewModel.history.value ?: emptyList()))
            } else {
                render(TracksState.Content(trackList))
            }
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
            render(TracksState.Empty(getString(R.string.nothing_found)))
        }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.History -> showHistory()
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty(getString(R.string.nothing_found))
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
        binding.historyLinearLayout.isVisible = false
        binding.searchLinearLayout.isVisible = true

        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showHistory() {
        binding.historyLinearLayout.isVisible = true
        binding.searchLinearLayout.isVisible = false
        binding.clearHistory.isVisible = true

        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false

        viewModel.updateSearchHistory()
        val history = viewModel.history.value?.toMutableList() ?: mutableListOf()
        historyAdapter.tracks = history
        historyAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        binding.searchLinearLayout.isVisible = true
        binding.searchPlaceholder.setImageResource(R.drawable.no_internet_light)
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.progressBar.isVisible = false
        showMessage(
            getString(R.string.connection_issues),
            getString(R.string.download_failed)
        )
    }

    private fun showEmpty(message: String) {
        binding.searchLinearLayout.isVisible = true
        binding.searchPlaceholder.setImageResource(R.drawable.nothing_found_light)
        binding.searchPlaceholder.visibility = View.VISIBLE
        binding.historyLinearLayout.isVisible = false
        binding.progressBar.isVisible = false
        showMessage(message, "")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        if (text.isNotBlank())
            viewModel.searchDebounce(text + " ")
        if (!viewModel.history.value.isNullOrEmpty() && text.isNullOrEmpty())
            render(TracksState.History(viewModel.history.value ?: emptyList()))
        else
            render(TracksState.Content(trackList))
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
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







