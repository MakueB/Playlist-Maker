package com.example.playlistmaker.di

import com.example.playlistmaker.App
import com.example.playlistmaker.library.ui.FavoritesViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {(trackId: Int) ->
        FavoritesViewModel(trackId)
    }

    viewModel { (playlistId: Int) ->
        PlaylistsViewModel(playlistId)
    }
}