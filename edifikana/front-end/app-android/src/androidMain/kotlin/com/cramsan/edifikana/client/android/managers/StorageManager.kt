package com.cramsan.edifikana.client.android.managers

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.cramsan.edifikana.client.android.BackgroundDispatcher
import com.cramsan.edifikana.client.android.run
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout


class StorageManager @Inject constructor(
    private val storage: FirebaseStorage,
    @BackgroundDispatcher
    private val background: CoroutineDispatcher,
    @ApplicationContext
    private val appContext: Context,
){
    suspend fun uploadFile(uri: Uri): Result<String> = background.run {
        // Create a storage reference from our app
        val storageRef = storage.reference

        val file = uri.toFile()

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(file.name)

        val stream = FileInputStream(file)
        val uploadTask = mountainsRef.putStream(stream)

        uploadFileImpl(uploadTask)
    }

    suspend fun uploadFile(data: ByteArray, filename: String): Result<String> = background.run {
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(filename)

        val uploadTask = mountainsRef.putBytes(data)

        uploadFileImpl(uploadTask)
    }

    suspend fun downloadFile(path: String): Result<File> = background.run {
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(path)

        val cacheFolder = File(appContext.externalMediaDirs.first().absolutePath + File.separator + "cache")
        cacheFolder.mkdirs()
        val localFile = File(cacheFolder.absolutePath + File.separator + path)
        localFile.createNewFile()

        mountainsRef.getFile(localFile).await()
        localFile
    }

    suspend fun getDownloadUrl(path: String): Result<String> = background.run {
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child(path)

        val result = mountainsRef.downloadUrl.await()

        result.toString()
    }

    private suspend fun uploadFileImpl(uploadTask: UploadTask): String {
        withTimeout(10.seconds) {
            while (!uploadTask.isComplete) {
                delay(1000)
            }
        }
        val uploadPath = uploadTask.result.metadata?.path ?: throw Exception("Failed to get download URL")

        return uploadPath
    }
}