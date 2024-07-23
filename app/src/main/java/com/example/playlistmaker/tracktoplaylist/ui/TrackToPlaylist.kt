package com.example.playlistmaker.tracktoplaylist.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackToPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackToPlaylist : Fragment() {

    companion object {
    }

    private val viewModel by viewModel<TrackToPlaylistViewModel>()

    private var _binding: FragmentTrackToPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackToPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


}