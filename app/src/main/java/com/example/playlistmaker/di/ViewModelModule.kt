package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.favorites.FavoritesViewModel
import com.example.playlistmaker.library.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.newplaylist.ui.NewPlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        FavoritesViewModel(get(), get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}