package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlaylistsFragment  : Fragment() {
    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: Int) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putInt(PLAYLIST_ID, playlistId)
            }
        }
    }

    private val PlaylistsViewModel: PlaylistsViewModel by viewModel {
        parametersOf(requireArguments().getInt(PLAYLIST_ID))
    }

    private lateinit var binding: FragmentPlaylistsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PlaylistsViewModel.playlistLiveData.observe(viewLifecycleOwner) {
            //в следующем спринте?
        }
    }
}