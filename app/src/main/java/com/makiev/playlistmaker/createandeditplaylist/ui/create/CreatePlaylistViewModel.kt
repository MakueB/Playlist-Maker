package com.makiev.playlistmaker.createandeditplaylist.ui.create

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makiev.playlistmaker.createandeditplaylist.domain.api.CreatePlaylistInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.models.Playlist
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val createPlaylistInteractor: CreatePlaylistInteractor,
    private val imageStorageInteractor: ImageStorageInteractor
) : ViewModel() {
    protected val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> get() = _playlistName

    protected val _playlistDescription = MutableLiveData<String>()
    val playlistDescription: LiveData<String> get() = _playlistDescription

    protected val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    protected val _isSaveButtonEnabled = MutableLiveData<Boolean>(false)
    val isSaveButtonEnabled: LiveData<Boolean> get() = _isSaveButtonEnabled

    open fun updatePlaylistName(name: String) {
        _playlistName.value = name
        _isSaveButtonEnabled.value = name.isNotBlank()
    }

    open fun updatePlaylistDescription(description: String) {
        _playlistDescription.value = description
    }

    open fun updateImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    open fun savePlaylist() {
        val name = _playlistName.value ?: return
        val description = _playlistDescription.value.orEmpty()
        val imageUri = _imageUri.value?.toString()

        val playlist = Playlist(0, name, description, imageUri, emptyList())
        viewModelScope.launch {
            createPlaylistInteractor.savePlaylist(playlist)
        }
    }

    open fun hasUnsavedChanges(): Boolean {
        return !_playlistName.value.isNullOrEmpty() ||
                !_playlistDescription.value.isNullOrEmpty() ||
                _imageUri.value != null
    }

    fun saveImageToPrivateStorage(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val savedUri = imageStorageInteractor.saveToPrivateStorage(uri, contentResolver)
            updateImageUri(savedUri)
        }
    }
}