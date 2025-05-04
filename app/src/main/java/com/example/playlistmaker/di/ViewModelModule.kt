package com.example.playlistmaker.di

import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.library.ui.favorites.FavoritesViewModel
import com.example.playlistmaker.library.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.createplaylist.ui.CreatePlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.details.ui.DetailsViewModel
import com.example.playlistmaker.editplaylist.ui.EditPlaylistViewModel
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
        CreatePlaylistViewModel(get())
    }
    viewModel {
        DetailsViewModel(get(),get())
    }

    viewModel { (playlist: Playlist) ->
        EditPlaylistViewModel(playlistToEdit = playlist, createPlaylistInteractor = get())
    }
}