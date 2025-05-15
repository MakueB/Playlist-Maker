package com.makiev.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.databinding.FragmentSearchBinding
import com.makiev.playlistmaker.main.ui.MainActivity
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
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

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var adapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null

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

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track: Track ->
            viewModel.saveToHistory(track)
            val action = SearchFragmentDirections.actionSearchFragmentToPlayerActivity(track)
            findNavController().navigate(action)
        }

        adapter = TrackAdapter (object : TrackActionListener {
            override fun onTrackClick(track: Track) {
                (activity as MainActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track): Boolean {
                return  false
            }
        })


        historyAdapter = TrackAdapter (object : TrackActionListener {
            override fun onTrackClick(track: Track) {
                (activity as MainActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track): Boolean {
                return false
            }
        })


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter?.tracks = trackList
        binding.recyclerView.adapter = adapter

        viewModel.updateSearchHistory()
        historyAdapter?.tracks = viewModel.history.value.toMutableList()
        binding.historyRecyclerView.adapter = historyAdapter

        binding.editText.requestFocus()
        showKeyboard(binding.editText)

        setupListeners()
        setupObservers()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.history.collect { historyList ->
                    if (historyList.isNotEmpty()) {
                        render(TracksState.History(historyList))
                    } else {
                        render(TracksState.Content(trackList))
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.toastState.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setupListeners() {
        binding.editText.addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                binding.imageViewClear.isVisible = !s.isNullOrEmpty()
                text = s.toString()

                if ((s?.length ?: -1) > 1) {//не начинать поиск при пустой строке ввода
                    viewModel.searchDebounce(s.toString())

                    if ((binding.editText.hasFocus()
                                && s?.isBlank() == true) && viewModel.history.value.isNotEmpty()
                    ) {
                        render(TracksState.History(viewModel.history.value))
                    } else {
                        render(TracksState.Content(trackList))
                    }
                } else {
                    if (viewModel.history.value.isNotEmpty())
                        render(TracksState.History(viewModel.history.value))
                    else
                        render(TracksState.Content(trackList))
                }
            }
        )
        binding.imageViewClear.setOnClickListener {
            binding.editText.setText("")
            trackList.clear()
            adapter?.notifyDataSetChanged()

            hideKeyboard(binding.editText)

            if (viewModel.history.value.isNotEmpty()) {
                render(TracksState.History(viewModel.history.value))
            } else {
                render(TracksState.Content(trackList))
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.search(binding.editText.text.toString())
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            historyAdapter?.notifyDataSetChanged()
            render(TracksState.Empty(R.string.nothing_found))
        }
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.History -> showHistory(state.tracks)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.historyLinearLayout.isVisible = false
        binding.searchLinearLayout.isVisible = true

        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(tracks: List<Track>) {
        binding.historyLinearLayout.isVisible = true
        binding.searchLinearLayout.isVisible = false
        binding.clearHistory.isVisible = true

        binding.searchPlaceholder.isVisible = false
        binding.somethingWrongTexView.isVisible = false
        binding.refreshButton.isVisible = false
        binding.progressBar.isVisible = false

        historyAdapter?.tracks = tracks.toMutableList()
        historyAdapter?.notifyDataSetChanged()
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            binding.somethingWrongTexView.visibility = View.VISIBLE
            trackList.clear()
            adapter?.notifyDataSetChanged()
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
            if (viewModel.history.value.isNotEmpty() && text.isEmpty())
                render(TracksState.History(viewModel.history.value))
            else
                render(TracksState.Content(trackList))
        }
    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        historyAdapter = null
        binding.recyclerView.adapter = null
        binding.historyRecyclerView.adapter = null
        _binding = null
    }
}







