package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.core.CoreUri
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

/**
 * Download strategy for JVM.
 */
class JvmDownloadStrategy : DownloadStrategy {

    override fun isFileCached(targetRef: String): Boolean {
        return getFileImp(targetRef) != null
    }

    override fun getCachedFile(targetRef: String): CoreUri {
        return getFileImp(targetRef) ?: throw RuntimeException("File not found")
    }

    private fun getFileImp(targetRef: String): CoreUri? {
        // Define the path of the directory
        val cacheDirectory = Paths.get(System.getProperty("user.home"), ".edifikana", ".cache")

        // Create the directory, including any necessary but nonexistent parent directories
        Files.createDirectories(cacheDirectory)

        val cachedFile = cacheDirectory.resolve(targetRef)
        return if (cachedFile.exists()) {
            CoreUri.createUri(cachedFile.absolutePathString())
        } else {
            null
        }
    }

    override fun saveToFile(data: ByteArray, targetRef: String): CoreUri {
        // Define the path of the directory
        val cacheDirectory = Paths.get(System.getProperty("user.home"), ".edifikana", ".cache")

        // Create the directory, including any necessary but nonexistent parent directories
        Files.createDirectories(cacheDirectory)

        val cachedFile = cacheDirectory.resolve(targetRef)
        File(cachedFile.absolutePathString()).writeBytes(data)

        return CoreUri.createUri(cachedFile.absolutePathString())
    }
}
