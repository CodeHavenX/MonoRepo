package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.utils.ASSET_2
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for StorageController.
 */

class StorageControllerTest :
    CoroutineTest(),
    KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `getSignedDownload should return HttpResponse with OK and signed URL`() =
        testBackEndApplication {
            // Arrange
            val expectedResponse = readFileContent("requests/get_signed_download_response.json")
            val storageService = get<StorageService>()
            coEvery { storageService.getSignedDownloadUrl(AssetId(ASSET_2.id.assetId)) } returns ASSET_2
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
            coEvery { contextRetriever.getContext(any()) }.answers { mockk() }

            // Act
            val response = client.get("storage/signed-download?asset_id=${ASSET_2.id.assetId}")

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `createSignedUpload should return HttpResponse with OK and signed upload URL`() =
        testBackEndApplication {
            // Arrange
            val expectedResponse = readFileContent("requests/create_signed_upload_response.json")
            val signedUrl = "https://storage.example.com/upload/signed/employee2.png"
            val path = "employee2.png"
            val storageService = get<StorageService>()
            coEvery { storageService.getSignedUploadUrl(ASSET_2.fileName) } returns Pair(signedUrl, path)
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
            coEvery { contextRetriever.getContext(any()) }.answers { mockk() }

            // Act
            val response = client.post("storage/signed-upload?filename=${ASSET_2.fileName}")

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }
}
