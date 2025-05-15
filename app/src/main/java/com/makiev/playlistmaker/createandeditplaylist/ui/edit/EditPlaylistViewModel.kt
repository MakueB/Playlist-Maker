package com.makiev.playlistmaker.createandeditplaylist.ui.edit

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.makiev.playlistmaker.createandeditplaylist.ui.create.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistToEdit: Playlist,
    createPlaylistInteractor: CreatePlaylistInteractor,
    imageStorageInteractor: ImageStorageInteractor
) : CreatePlaylistViewModel(createPlaylistInteractor, imageStorageInteractor) {

    init {
        populateInitialData()
    }

    fun populateInitialData() {
        updatePlaylistName(playlistToEdit.name)
        updatePlaylistDescription(playlistToEdit.description)
        updateImageUri(playlistToEdit.imageUrl?.toUri())
    }

    override fun savePlaylist() {
        val updated = buildUpdatedPlaylist()

        viewModelScope.launch {
            createPlaylistInteractor.savePlaylist(updated)
        }
    }

    fun updateAndGetPlaylist(): Playlist {
        val updated = buildUpdatedPlaylist()

        viewModelScope.launch {
            createPlaylistInteractor.savePlaylist(updated)
        }
        return updated
    }

    private fun buildUpdatedPlaylist(): Playlist {
        val name = _playlistName.value ?: playlistToEdit.name
        val description = _playlistDescription.value.orEmpty()
        val imageUri = _imageUri.value?.toString()

        return playlistToEdit.copy(
            name = name,
            description = description,
            imageUrl = imageUri
        )
    }
}