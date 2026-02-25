package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.FileUploadResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import io.github.jan.supabase.exceptions.RestException
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import org.junit.jupiter.api.BeforeEach

/**
 * Unit tests for [StorageServiceImpl].
 */
class StorageServiceImplTest : CoroutineTest() {

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    /**
     * Test that uploadFile with valid data returns success with storage reference.
     */
    @Test
    fun `uploadFile with valid data returns storage reference`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        val mockUploadResponse = mockk<FileUploadResponse>(relaxed = true)
        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } returns mockUploadResponse

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(targetRef, result.getOrNull())
    }

    /**
     * Test that uploadFile with 400 error throws InvalidRequestException.
     */
    @Test
    fun `uploadFile with 400 error throws InvalidRequestException`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        val mockResponse = mockk<HttpResponse>(relaxed = true) {
            every { status } returns HttpStatusCode.BadRequest
        }
        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } throws RestException(
            "Bad Request",
            "400 Error",
            mockResponse
        )

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ClientRequestExceptions.InvalidRequestException)
        assertTrue(exception.message?.contains("Invalid file format") == true)
    }

    /**
     * Test that uploadFile with 401 error throws UnauthorizedException.
     */
    @Test
    fun `uploadFile with 401 error throws UnauthorizedException`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        val mockResponse = mockk<HttpResponse>(relaxed = true) {
            every { status } returns HttpStatusCode.Unauthorized
        }
        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } throws RestException(
            "Unauthorized",
            "401 Error",
            mockResponse
        )

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ClientRequestExceptions.UnauthorizedException)
        assertTrue(exception.message?.contains("Authentication required") == true)
    }

    /**
     * Test that uploadFile with 403 error throws ForbiddenException.
     */
    @Test
    fun `uploadFile with 403 error throws ForbiddenException`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        val mockResponse = mockk<HttpResponse>(relaxed = true) {
            every { status } returns HttpStatusCode.Forbidden
        }
        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } throws RestException(
            "Forbidden",
            "403 Error",
            mockResponse
        )

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ClientRequestExceptions.ForbiddenException)
        assertTrue(exception.message?.contains("Permission denied") == true)
    }

    /**
     * Test that uploadFile with 409 error throws ConflictException.
     */
    @Test
    fun `uploadFile with 409 error throws ConflictException`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        val mockResponse = mockk<HttpResponse>(relaxed = true) {
            every { status } returns HttpStatusCode.Conflict
        }
        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } throws RestException(
            "Conflict",
            "409 Error",
            mockResponse
        )

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ClientRequestExceptions.ConflictException)
        assertTrue(exception.message?.contains("File already exists") == true)
    }

    /**
     * Test that uploadFile with unexpected error throws InvalidRequestException with generic message.
     */
    @Test
    fun `uploadFile with unexpected error throws InvalidRequestException`() = runTest {
        // Arrange
        val storage = mockk<Storage>()
        val bucket = mockk<BucketApi>()
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(storage, downloadStrategy)
        val testData = "test content".toByteArray()
        val targetRef = "private/properties/test.jpg"

        coEvery { storage.from(any<String>()) } returns bucket
        coEvery { bucket.upload(any<String>(), any<ByteArray>(), any()) } throws Exception("Unexpected error")

        // Act
        val result = storageService.uploadFile(testData, targetRef)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ClientRequestExceptions.InvalidRequestException)
        assertTrue(exception.message?.contains("Upload failed") == true)
    }
}
