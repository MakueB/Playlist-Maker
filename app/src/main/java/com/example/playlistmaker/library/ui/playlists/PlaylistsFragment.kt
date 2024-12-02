package com.example.playlistmaker.library.ui.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.LibraryFragmentDirections
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private var adapter: PlaylistsAdapter? = null
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNewPlaylistFragment())
        }

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist: Playlist ->
            val action =
                LibraryFragmentDirections.actionLibraryFragmentToPlaylistDetailsFragment(playlist)
            findNavController().navigate(action)
        }

        adapter = PlaylistsAdapter(
            onItemClick = { playlist: Playlist ->
                (activity as MainActivity).animateBottomNavigationView()
                onPlaylistClickDebounce(playlist)
            },
            onItemLongClick = { playlist: Playlist ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Удаление плейлиста")
                    .setMessage("Вы действительно хотите удалить плейлист \"${playlist.name}\"?")
                    .setPositiveButton("Удалить") { dialog, _ ->
                        deletePlaylist(playlist) // Вызов функции удаления
                        dialog.dismiss()
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
                true
            }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        viewModel.getPlaylistsAll()
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun deletePlaylist(playlist: Playlist) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deletePlaylist(playlist)
            adapter?.notifyDataSetChanged()
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

        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
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