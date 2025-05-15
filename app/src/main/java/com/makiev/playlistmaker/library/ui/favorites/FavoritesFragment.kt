package com.makiev.playlistmaker.library.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.databinding.FragmentFavoritesBinding
import com.makiev.playlistmaker.library.ui.LibraryFragmentDirections
import com.makiev.playlistmaker.main.ui.MainActivity
import com.makiev.playlistmaker.search.domain.models.Track
import com.makiev.playlistmaker.search.ui.TrackActionListener
import com.makiev.playlistmaker.search.ui.TrackAdapter
import com.makiev.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var adapter: TrackAdapter? = null

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track: Track ->
            favoritesViewModel.saveToHistory(track)

            val action = LibraryFragmentDirections.actionLibraryFragmentToPlayerActivity(track)
            findNavController().navigate(action)
        }

        adapter = TrackAdapter(object : TrackActionListener {
            override fun onTrackClick(track: Track) {
                (activity as MainActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track): Boolean {
                return false
            }

        })
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.adapter = adapter

        favoritesViewModel.getFavorites()
        favoritesViewModel.favoritesState.observe(viewLifecycleOwner){
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        favoritesViewModel.getFavorites()
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> showEmpty(state.message)
            is FavoritesState.Content -> showContent(state.tracks)
            else -> showEmpty(getString(R.string.library_is_empty))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.apply {
            favoritesPlaceholder.isVisible = false
            somethingWrongTexView.isVisible = false
            favoritesRecyclerView.isVisible = true
        }

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun showEmpty(message: String) {
        binding.apply {
            favoritesPlaceholder.isVisible = true
            somethingWrongTexView.isVisible = true
            favoritesRecyclerView.isVisible = false

            somethingWrongTexView.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.favoritesRecyclerView.adapter = null
    }
}