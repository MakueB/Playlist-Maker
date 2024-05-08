package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.ui.FavoritesViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {(track: Track) ->
        FavoritesViewModel(track)
    }

    viewModel { (playlist: Playlist) ->
        PlaylistsViewModel(playlist)
    }
}