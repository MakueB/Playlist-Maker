package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding: FragmentLibraryBinding
        get() = _binding!!

    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackId = arguments?.getInt("trackId", -1) ?: -1
        val playlist = arguments?.getInt("playlistId", -1) ?: -1//сюда данные нужно будет получать по-другому

        binding.viewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle, trackId, playlist)

        tabMediator = TabLayoutMediator(binding.libraryTabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }

        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroy()
        tabMediator.detach()
        _binding = null
    }
}