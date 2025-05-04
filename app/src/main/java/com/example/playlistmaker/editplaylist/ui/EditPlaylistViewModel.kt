package com.example.playlistmaker.editplaylist.ui

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.createplaylist.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.createplaylist.ui.CreatePlaylistViewModel
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
}