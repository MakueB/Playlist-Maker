package com.example.playlistmaker.library.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.library.ui.favorites.FavoritesFragment
import com.example.playlistmaker.library.ui.playlists.PlaylistsFragment

class LibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
    private val trackId: Int, private val playlistId: Int) : FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            else -> PlaylistsFragment()
        }
    }
}