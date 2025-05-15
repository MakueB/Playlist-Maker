package com.makiev.playlistmaker.di

import com.makiev.playlistmaker.library.domain.favorites.api.FavoritesInteractor
import com.makiev.playlistmaker.library.domain.favorites.impl.FavoritesInteractorImpl
import com.makiev.playlistmaker.library.domain.playlists.api.PlaylistsInteractor
import com.makiev.playlistmaker.library.domain.playlists.impl.PlaylistsInteractorImpl
import com.makiev.playlistmaker.createandeditplaylist.domain.CreatePlaylistInteractorImpl
import com.makiev.playlistmaker.createandeditplaylist.domain.ImageStorageInteractorImpl
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageInteractor
import com.makiev.playlistmaker.player.domain.api.PlayerInteractor
import com.makiev.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.makiev.playlistmaker.details.domain.api.DetailsInteractor
import com.makiev.playlistmaker.details.domain.impl.DetailsInteractorImpl
import com.makiev.playlistmaker.search.domain.api.TracksInteractor
import com.makiev.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.makiev.playlistmaker.settings.domain.api.SettingsInteractor
import com.makiev.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.makiev.playlistmaker.sharing.domain.api.SharingInteractor
import com.makiev.playlistmaker.sharing.domain.impl.SharingInteractorImpl
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
        DetailsInteractorImpl(get(), get())
    }

    single <ImageStorageInteractor> {
        ImageStorageInteractorImpl(get())
    }
}