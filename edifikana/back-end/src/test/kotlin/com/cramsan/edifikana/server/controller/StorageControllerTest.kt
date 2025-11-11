package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.network.CreateAssetNetworkRequest
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.utils.ASSET_1
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
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
@OptIn(NetworkModel::class)
class StorageControllerTest : CoroutineTest(), KoinTest {
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
    fun `createAsset should return HttpResponse with OK and asset`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/create_asset_response.json")
        val call = mockk<ApplicationCall>(relaxed = true)
        val request = CreateAssetNetworkRequest("dummy.png", byteArrayOf(1, 2, 3))
        coEvery { call.receive<CreateAssetNetworkRequest>() } returns request
        val storageService = get<StorageService>()
        coEvery {
            storageService.createAsset(
                fileName = any(),
                content = any()
            )
        } returns ASSET_1
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers { mockk() }

        // Act
        val response = client.post("storage?filename=${ASSET_1.fileName}") {
            setBody(ASSET_1.content)
            contentType(ContentType.Image.PNG)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
