package com.cramsan.edifikana.client.lib.service

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.framework.assertlib.assert
import com.cramsan.framework.core.CoreUri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class FirebaseStorageService(
    private val storage: FirebaseStorage,
    private val context: Context,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: StorageRef): Result<StorageRef> = runSuspendCatching {
// Create a storage reference from our app
        val storageReference = storage.reference

        // Create a reference to the file to be uploaded
        val uploadRef = storageReference.child(targetRef.ref)

        // Start uploading bytes async.
        val uploadTask = uploadRef.putBytes(data)

        // TODO: Make timeout configurable
        // Wait until the upload is completed
        withTimeout(20.seconds) {
            while (!uploadTask.isComplete) {
                delay(1.seconds)
            }
        }

        // Get the imageRef of the uploaded file
        // TODO: Improve how to generate the references and classes
        val uploadPathRef = uploadTask.result.metadata?.path?.let {
            storageReference.child(it)
        } ?: throw RuntimeException("Failed to get download URL")
        assert(uploadPathRef.path == uploadRef.path, TAG, "Path mismatch for uploaded file.")

        // TODO: Improve how to generate the references and classes
        // We are manually removing the leading slash from the path since this is how the BE expects the path
        val resultRef = StorageRef(uploadPathRef.path.removePrefix("/"))
        resultRef
    }

    override suspend fun downloadImage(targetRef: StorageRef): Result<CoreUri> = runSuspendCatching {
        val resolver = context.contentResolver
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

        // Cache was found
        if (cachedUri != null) {
            return@runSuspendCatching CoreUri(cachedUri)
        }

        // 2. Download the image from storage and generate a local uri

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val downloadRef = storageRef.child(targetRef.ref)

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, targetRef.filename())
        }

        val newImageUri = resolver.insert(imageCollection, newImageDetails) ?: throw RuntimeException(
            "Failed to create new image"
        )

        // TODO: Handle any filesize
        val data = downloadRef.getBytes(ONE_MEGABYTE).await()

        context.contentResolver.openOutputStream(newImageUri).use {
            it?.write(data)
        }

        CoreUri(newImageUri)
    }

    companion object {
        private const val TAG = "FirebaseStorageService"
        private const val ONE_MEGABYTE = 1024 * 1024L
    }
}
