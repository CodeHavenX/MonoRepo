package com.cramsan.edifikana.client.lib.service

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.framework.core.CoreUri

class AndroidDownloadStrategy(
    private val context: Context,
) : DownloadStrategy {
    override fun isFileCached(targetRef: StorageRef): Boolean {
        return getFileImp(targetRef) != null
    }

    override fun getCachedFile(targetRef: StorageRef): CoreUri {
        return getFileImp(targetRef) ?: throw RuntimeException("File not found")
    }

    private fun getFileImp(targetRef: StorageRef): CoreUri? {
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        // 1. First we check the local content to see if the file is already cached.

        // Only want to retrieve _ID and DISPLAY_NAME columns
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )

        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(targetRef.filename())

        //  Run query
        val cachedUri = context.contentResolver.query(
            imageCollection,
            projection,
            selection,
            selectionArgs,
            null,
            null,
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)

            // iterating over the found images. If at least one file was found, then there is a cache hit.
            var uri: Uri? = null
            while (cursor.moveToNext()) {
                val imageId = cursor.getString(idColumn)
                uri = Uri.withAppendedPath(imageCollection, imageId)
                if (uri != null) {
                    break
                }
            }
            uri
        }
        return cachedUri?.let { CoreUri(it) }
    }

    override fun saveToFile(data: ByteArray, targetRef: StorageRef): CoreUri {
        val resolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, targetRef.filename())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val newImageUri = resolver.insert(imageCollection, newImageDetails) ?: throw RuntimeException(
            "Failed to create new image"
        )

        context.contentResolver.openOutputStream(newImageUri).use {
            it?.write(data)
        }

        return CoreUri(newImageUri)
    }
}
