package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class SearchFragment : Fragment() {
    companion object {
        private const val TEXT = "TEXT_DEF"
        private const val TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text = TEXT_DEF
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val trackList = mutableListOf<Track>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onTrackClickListener = { track: Track ->
            if (clickDebounce()) {
                val trackWasSavedMessage = getString(R.string.track_was_saved)
                val listContainsTrack =
                    viewModel.history.value?.any { it.trackId == track.trackId } ?: false

                viewModel.saveToHistory(track)

                if (!listContainsTrack)
                    Toast.makeText(requireContext(), trackWasSavedMessage, Toast.LENGTH_SHORT)
                        .show()

                val action = SearchFragmentDirections.actionSearchFragmentToPlayerActivity(track)
                findNavController().navigate(action)
            }
        }

        adapter = TrackAdapter(onTrackClickListener)
        historyAdapter = TrackAdapter(onTrackClickListener)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        } else {
            render(TracksState.Content(trackList))
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.toastState.observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.history.observe(viewLifecycleOwner) {
            historyAdapter.tracks = it.toMutableList()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun setupListeners() {
        binding.editText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                binding.imageViewClear.isVisible = !s.isNullOrEmpty()
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT, text)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(TEXT, TEXT_DEF)
            binding.editText.setText(text)
            if (text.isNotBlank())
                viewModel.searchDebounce("$text ")
            if (!viewModel.history.value.isNullOrEmpty() && text.isEmpty())
                render(TracksState.History(viewModel.history.value ?: emptyList()))
            else
                render(TracksState.Content(trackList))
        }
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







