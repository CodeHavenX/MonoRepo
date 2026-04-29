package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [StorageServiceImpl].
 */
class StorageServiceImplTest : CoroutineTest() {

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun buildHttpClient(mockEngine: MockEngine): HttpClient =
        HttpClient(mockEngine) {
            install(ContentNegotiation) { json(createJson()) }
            defaultRequest { url("http://localhost/") }
        }

    @Test
    fun `uploadFile returns assetId on success`() = runTest {
        // Arrange
        val targetRef = "timecard-images/employee.png"
        val bucketId = "images"
        val testData = "test content".toByteArray()
        val signedUploadUrl = "https://storage.example.com/upload/signed/employee.png"
        val expectedAssetId = "images/timecard-images/employee.png"
        val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("signed-upload") ->
                    respond(
                        """{"signed_url":"$signedUploadUrl","path":"$targetRef","asset_id":"$expectedAssetId"}""",
                        HttpStatusCode.OK,
                        jsonHeaders,
                    )
                else -> respond("", HttpStatusCode.OK)
            }
        }

        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(buildHttpClient(mockEngine), downloadStrategy)

        // Act
        val result = storageService.uploadFile(testData, targetRef, bucketId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedAssetId, result.getOrNull())
    }

    @Test
    fun `uploadFile returns failure on network error`() = runTest {
        // Arrange
        val mockEngine = MockEngine { _ -> throw Exception("Network error") }
        val downloadStrategy = mockk<DownloadStrategy>(relaxed = true)
        val storageService: StorageService = StorageServiceImpl(buildHttpClient(mockEngine), downloadStrategy)

        // Act
        val result = storageService.uploadFile("test".toByteArray(), "file.png", "images")

        // Assert
        assertTrue(result.isFailure)
    }

    @Test
    fun `downloadFile returns cached file without making HTTP calls`() = runTest {
        // Arrange
        val targetRef = "images/timecard-images/employee.png"
        val cachedUri = mockk<CoreUri>()
        val downloadStrategy = mockk<DownloadStrategy> {
            coEvery { isFileCached(targetRef) } returns true
            coEvery { getCachedFile(targetRef) } returns cachedUri
        }
        val mockEngine = MockEngine { _ -> error("Should not make HTTP calls for cached files") }
        val storageService: StorageService = StorageServiceImpl(buildHttpClient(mockEngine), downloadStrategy)

        // Act
        val result = storageService.downloadFile(targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(cachedUri, result.getOrNull())
    }

    @Test
    fun `downloadFile fetches signed URL then downloads bytes`() = runTest {
        // Arrange
        val targetRef = "images/timecard-images/employee.png"
        val signedDownloadUrl = "https://storage.example.com/download/signed/employee.png"
        val fileBytes = "file content".toByteArray()
        val savedUri = mockk<CoreUri>()
        val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("signed-download") ->
                    respond(
                        """{"id":"$targetRef","file_name":"employee.png","signed_url":"$signedDownloadUrl"}""",
                        HttpStatusCode.OK,
                        jsonHeaders,
                    )
                else -> respond(fileBytes, HttpStatusCode.OK)
            }
        }

        val downloadStrategy = mockk<DownloadStrategy> {
            coEvery { isFileCached(targetRef) } returns false
            coEvery { saveToFile(any(), targetRef) } returns savedUri
        }
        val storageService: StorageService = StorageServiceImpl(buildHttpClient(mockEngine), downloadStrategy)

        // Act
        val result = storageService.downloadFile(targetRef)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(savedUri, result.getOrNull())
    }

    @Test
    fun `downloadFile returns failure on network error`() = runTest {
        // Arrange
        val targetRef = "images/timecard-images/employee.png"
        val mockEngine = MockEngine { _ -> throw Exception("Network error") }
        val downloadStrategy = mockk<DownloadStrategy> {
            coEvery { isFileCached(targetRef) } returns false
        }
        val storageService: StorageService = StorageServiceImpl(buildHttpClient(mockEngine), downloadStrategy)

        // Act
        val result = storageService.downloadFile(targetRef)

        // Assert
        assertTrue(result.isFailure)
    }
}
