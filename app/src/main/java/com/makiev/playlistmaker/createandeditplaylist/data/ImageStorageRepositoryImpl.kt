package com.makiev.playlistmaker.createandeditplaylist.data

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.makiev.playlistmaker.createandeditplaylist.domain.api.ImageStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageStorageRepositoryImpl(private val context: Context) : ImageStorageRepository {
    override suspend fun saveToPrivateStorage(
        uri: Uri,
        contentResolver: ContentResolver
    ): Uri {
        return withContext (Dispatchers.IO) {
            val inputStream = contentResolver.openInputStream(uri) ?: throw IOException("Can't open input stream from URI")

            val filePath = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Images"
            ).apply { mkdirs() }

            val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            inputStream.close()
            outputStream.close()

            file.toUri()
        }
    }
}