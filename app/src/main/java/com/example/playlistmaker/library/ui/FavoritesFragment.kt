package com.example.playlistmaker.library.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {
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

        adapter = TrackAdapter()
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.adapter = adapter

        favoritesViewModel.getFavorites()
        favoritesViewModel.favoritesState.observe(viewLifecycleOwner){
            render(it)
        }
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