package com.example.playlistmaker.newplaylist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.newplaylist.domain.api.NewPlaylistInteractor
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel (
    private val newPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {
    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> get() = _playlistName

    private val _playlistDescription = MutableLiveData<String>()
    val playlistDescription: LiveData<String> get() = _playlistDescription

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _isSaveButtonEnabled = MutableLiveData<Boolean>()
    val isSaveButtonEnabled: LiveData<Boolean> get() = _isSaveButtonEnabled

    init {
        _isSaveButtonEnabled.value = false
    }

    fun updatePlaylistName(name: String) {
        _playlistName.value = name
        _isSaveButtonEnabled.value = name.isNotBlank()
    }

    fun updatePlaylistDescription(description: String) {
        _playlistDescription.value = description
    }

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    fun savePlaylist() {
        val name = _playlistName.value ?: return
        val description = _playlistDescription.value.orEmpty()
        val imageUri = _imageUri.value?.toString()

        val playlist = Playlist(0, name, description, imageUri, emptyList())
        viewModelScope.launch {
            newPlaylistInteractor.savePlaylist(playlist)
        }
    }
}