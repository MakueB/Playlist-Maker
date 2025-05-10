package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.favorites.api.FavoritesInteractor
import com.example.playlistmaker.library.domain.favorites.impl.FavoritesInteractorImpl
import com.example.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.example.playlistmaker.library.domain.playlists.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.createandeditplaylist.domain.CreatePlaylistInteractorImpl
import com.example.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.details.domain.api.DetailsInteractor
import com.example.playlistmaker.details.domain.impl.DetailsInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single <TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    single <SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single <SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory <PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single <FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<CreatePlaylistInteractor> {
        CreatePlaylistInteractorImpl(get())
    }

    single <PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    single<DetailsInteractor> {
        DetailsInteractorImpl(get(),get())
    }
}