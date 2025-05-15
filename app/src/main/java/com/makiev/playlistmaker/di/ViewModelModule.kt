package com.makiev.playlistmaker.di

import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.library.ui.favorites.FavoritesViewModel
import com.makiev.playlistmaker.library.ui.playlists.PlaylistsViewModel
import com.makiev.playlistmaker.createandeditplaylist.ui.create.CreatePlaylistViewModel
import com.makiev.playlistmaker.player.ui.PlayerViewModel
import com.makiev.playlistmaker.details.ui.DetailsViewModel
import com.makiev.playlistmaker.createandeditplaylist.ui.edit.EditPlaylistViewModel
import com.makiev.playlistmaker.search.ui.SearchViewModel
import com.makiev.playlistmaker.settings.ui.SettingsViewModel
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
        CreatePlaylistViewModel(get(), get())
    }
    viewModel {
        DetailsViewModel(get(), get(), get())
    }

    viewModel { (playlist: Playlist) ->
        EditPlaylistViewModel(
            playlistToEdit = playlist,
            createPlaylistInteractor = get(),
            imageStorageInteractor = get()
        )
    }
}