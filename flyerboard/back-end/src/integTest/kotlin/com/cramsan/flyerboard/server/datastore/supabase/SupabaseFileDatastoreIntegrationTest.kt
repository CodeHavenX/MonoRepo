package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.framework.utils.uuid.UUID
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.util.Base64
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Integration tests for [SupabaseFileDatastore] against a real Supabase Storage instance.
 */
class SupabaseFileDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var filePath: String
    private val http = HttpClient(CIO)

    @BeforeTest
    fun setup() {
        filePath = "${UUID.random()}.png"
    }

    @AfterTest
    fun closeHttpClient() {
        http.close()
    }

    @Test
    fun `createSignedUploadUrl should return a signed URL and token`() =
        runBlocking {
            val result = fileDatastore.createSignedUploadUrl(filePath)

            assertTrue(result.isSuccess)
            val upload = result.getOrThrow()
            assertTrue(upload.signedUrl.isNotBlank())
        }

    @Test
    fun `getSignedUrl should succeed for an uploaded file`() =
        runBlocking {
            uploadTestFile()

            val result = fileDatastore.getSignedUrl(filePath)

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isNotBlank())

            fileDatastore.deleteFile(filePath)
            Unit
        }

    @Test
    fun `deleteFile should remove an uploaded file`() =
        runBlocking {
            uploadTestFile()

            val result = fileDatastore.deleteFile(filePath)

            assertTrue(result.isSuccess)
            assertTrue(fileDatastore.getSignedUrl(filePath).isFailure)
        }

    private suspend fun uploadTestFile() {
        val upload = fileDatastore.createSignedUploadUrl(filePath).getOrThrow()
        http.put(upload.signedUrl) {
            contentType(ContentType.Image.PNG)
            setBody(MINIMAL_PNG_BYTES)
        }
    }

    companion object {
        // A minimal valid 1x1 transparent PNG, used so Storage's mime-type allowlist accepts the upload.
        private val MINIMAL_PNG_BYTES =
            Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==",
            )
    }
}
