package com.makiev.playlistmaker.createandeditplaylist.domain

import android.content.ContentResolver
import android.net.Uri
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageInteractor
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageRepository

class ImageStorageInteractorImpl(private val imageStorageRepository: ImageStorageRepository) :
    ImageStorageInteractor {
    override suspend fun saveToPrivateStorage(
        uri: Uri,
        contentResolver: ContentResolver
    ): Uri {
        return imageStorageRepository.saveToPrivateStorage(uri, contentResolver)
    }
}