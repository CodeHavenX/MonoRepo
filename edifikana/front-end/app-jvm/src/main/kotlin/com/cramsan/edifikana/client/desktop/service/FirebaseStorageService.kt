package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.framework.core.CoreUri
import dev.gitlive.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

@Suppress("UnusedPrivateProperty")
class FirebaseStorageService(
    private val storage: FirebaseStorage,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: StorageRef): Result<StorageRef> = runSuspendCatching {
        TODO()
    }

    override suspend fun downloadImage(targetRef: StorageRef): Result<CoreUri> = runSuspendCatching {
        // Define the path of the directory
        val cacheDirectory = Paths.get(System.getProperty("user.home"), ".edifikana", ".cache")

        // Create the directory, including any necessary but nonexistent parent directories
        Files.createDirectories(cacheDirectory)

        // 1. First we check the local content to see if the file is already cached.
        val cachedFile = cacheDirectory.resolve(targetRef.filename())
        if (cachedFile.exists()) {
            return@runSuspendCatching CoreUri.createUri(cachedFile.absolutePathString())
        }

        // 2. Download the image from storage and generate a local uri

        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a reference to "mountains.jpg"
        val downloadRef = storageRef.child(targetRef.ref)

        val downloadUrl = downloadRef.getDownloadUrl()

        val inputStream: InputStream = URL(downloadUrl).openStream()
        Files.copy(inputStream, cachedFile, StandardCopyOption.REPLACE_EXISTING)

        CoreUri.createUri(cachedFile.absolutePathString())
    }

    companion object {
        private const val TAG = "FirebaseStorageService"
    }
}
