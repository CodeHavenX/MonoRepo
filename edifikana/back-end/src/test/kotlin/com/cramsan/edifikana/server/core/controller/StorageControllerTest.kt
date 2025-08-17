 package com.cramsan.edifikana.server.core.controller

 import com.cramsan.edifikana.lib.model.AssetId
 import com.cramsan.edifikana.lib.model.UserId
 import com.cramsan.edifikana.lib.model.network.CreateAssetNetworkRequest
 import com.cramsan.edifikana.server.core.controller.auth.ClientContext
 import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
 import com.cramsan.edifikana.server.core.datastore.dummy.ASSET_1
 import com.cramsan.edifikana.server.core.datastore.dummy.ASSET_2
 import com.cramsan.edifikana.server.core.service.StorageService
 import com.cramsan.edifikana.server.core.service.models.Asset
 import com.cramsan.framework.annotations.NetworkModel
 import com.cramsan.framework.core.ktor.HttpResponse
 import com.cramsan.framework.test.CoroutineTest
 import io.ktor.client.call.body
 import io.ktor.client.request.get
 import io.ktor.client.request.headers
 import io.ktor.client.request.post
 import io.ktor.client.request.setBody
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
 class StorageControllerTest: CoroutineTest(), KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `createAsset should return HttpResponse with OK and asset`() = testEdifikanaApplication {
        // Arrange
        val call = mockk<ApplicationCall>(relaxed = true)
        val request = CreateAssetNetworkRequest("dummy.png", byteArrayOf(1, 2, 3))
        coEvery { call.receive<CreateAssetNetworkRequest>() } returns request
        val storageService = get<StorageService>()
        coEvery { storageService.createAsset(
            fileName = any(),
            content = any())
        } returns ASSET_1
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers { mockk() }

        // Act
        val response = client.post("storage") {
            headers{append("fileName",ASSET_1.fileName)}
            setBody(ASSET_1.content)
            contentType(ContentType.Image.PNG)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ASSET_1.fileName, (response.body() as Asset).fileName)
    }

    @Test
    fun `getAsset should return HttpResponse with OK and asset`() = testEdifikanaApplication {
        // Arrange
        val call = mockk<ApplicationCall>(relaxed = true)
        val assetId = AssetId("dummy_id")
        coEvery { call.parameters["id"] } returns assetId.toString()
        val storageService = get<StorageService>()
        coEvery { storageService.getAsset(any()) } returns ASSET_2
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers { mockk() }

        // Act
        val response = client.get("storage?asset_id=images/timecard-images/vicuna.png")


        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ASSET_2.fileName, (response.body() as Asset).fileName)
    }
 }
