package com.example.playlistmaker.library.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.LibraryFragmentDirections
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment  : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private var adapter: PlaylistsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNewPlaylistFragment())
        }

        adapter = PlaylistsAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        viewModel.getPlaylistsAll()
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
            else -> showEmpty(getString(R.string.library_is_empty))
        }
    }

    private fun showEmpty(message: String) {
        binding.somethingWrongTexView.isVisible = true
        binding.placeholder.isVisible = true
        binding.recyclerView.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.somethingWrongTexView.isVisible = false
        binding.placeholder.isVisible = false
        binding.recyclerView.isVisible = true
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistsAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.recyclerView.adapter = null
    }
}