package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.framework.core.CoreUri
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.FileUploadResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [StorageServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 *
 */
@Ignore
class StorageServiceImplTest {
    private val storage = mockk<Storage>()
    private val downloadStrategy = mockk<DownloadStrategy>()
    private val bucketApi = mockk<BucketApi>()
    private val service = StorageServiceImpl(storage, downloadStrategy)

    /**
     * Tests that uploadFile uploads data and returns the target reference.
     */
    @Test
    fun `uploadFile should upload data and return targetRef`() = runTest {
        // Arrange
        val data = byteArrayOf(1, 2, 3)
        val targetRef = "file.txt"
        coEvery { storage.from(any()) } returns bucketApi
        coEvery { bucketApi.upload(targetRef, data, any()) } returns FileUploadResponse(
            mockk(),
            mockk(),
            mockk()
        )

        // Act
        val result = service.uploadFile(data, targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(targetRef, result.getOrNull())
        coVerify { bucketApi.upload(targetRef, data, any()) }
    }

    /**
     * Tests that downloadFile returns cached file if available, otherwise downloads and saves it.
     */
    @Test
    fun `downloadFile should return cached file if available`() = runTest {
        // Arrange
        val targetRef = "file.txt"
        val uri = mockk<CoreUri>()
        coEvery { downloadStrategy.isFileCached(targetRef) } returns true
        coEvery { downloadStrategy.getCachedFile(targetRef) } returns uri

        // Act
        val result = service.downloadFile(targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(uri, result.getOrNull())
        coVerify { downloadStrategy.getCachedFile(targetRef) }
    }

    /**
     * Tests that downloadFile downloads, saves, and returns uri if not cached.
     */
    @Test
    fun `downloadFile should download, save, and return uri if not cached`() = runTest {
        // Arrange
        val targetRef = "file.txt"
        val bytes = byteArrayOf(1, 2, 3)
        val uri = mockk<CoreUri>()
        coEvery { downloadStrategy.isFileCached(targetRef) } returns false
        coEvery { storage.from(any()) } returns bucketApi
        coEvery { bucketApi.downloadAuthenticated(targetRef) } returns bytes
        coEvery { downloadStrategy.saveToFile(bytes, targetRef) } returns uri

        // Act
        val result = service.downloadFile(targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(uri, result.getOrNull())
        coVerify { bucketApi.downloadAuthenticated(targetRef) }
        coVerify { downloadStrategy.saveToFile(bytes, targetRef) }
    }
}

