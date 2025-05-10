package com.example.playlistmaker.createandeditplaylist.ui.edit

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.createandeditplaylist.domain.models.Playlist
import com.example.playlistmaker.createandeditplaylist.ui.create.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistToEdit: Playlist,
    createPlaylistInteractor: CreatePlaylistInteractor
) : CreatePlaylistViewModel(createPlaylistInteractor) {

    init {
        updatePlaylistName(playlistToEdit.name)
        updatePlaylistDescription(playlistToEdit.description)
        updateImageUri(playlistToEdit.imageUrl?.toUri())
    }

    override fun savePlaylist() {
        val name = _playlistName.value ?: return
        val description = _playlistDescription.value.orEmpty()
        val imageUri = _imageUri.value?.toString()

        val updated = playlistToEdit.copy(
            name = name,
            description = description,
            imageUrl = imageUri
        )

        viewModelScope.launch {
            createPlaylistInteractor.savePlaylist(updated)
        }
    }

    fun updateAndGetPlaylist(): Playlist {
        val name = _playlistName.value ?: return playlistToEdit
        val description = _playlistDescription.value.orEmpty()
        val imageUri = _imageUri.value?.toString()

        val updated = playlistToEdit.copy(
            name = name,
            description = description,
            imageUrl = imageUri
        )

        viewModelScope.launch {
            createPlaylistInteractor.savePlaylist(updated)
        }
        return updated
    }
}