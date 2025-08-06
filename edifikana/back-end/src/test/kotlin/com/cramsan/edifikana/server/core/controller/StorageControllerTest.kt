//package com.cramsan.edifikana.server.core.controller
//
//import com.cramsan.edifikana.lib.model.AssetId
//import com.cramsan.edifikana.lib.model.network.CreateAssetNetworkRequest
//import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
//import com.cramsan.edifikana.server.core.service.StorageService
//import com.cramsan.edifikana.server.core.service.models.Asset
//import com.cramsan.framework.core.ktor.HttpResponse
//import com.cramsan.framework.test.CoroutineTest
//import io.ktor.http.HttpStatusCode
//import io.ktor.server.application.ApplicationCall
//import io.ktor.server.request.receive
//import io.mockk.coEvery
//import io.mockk.mockk
//import org.koin.core.context.stopKoin
//import org.koin.test.KoinTest
//import kotlin.test.AfterTest
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertIs
//import kotlin.time.ExperimentalTime
//
///**
// * Unit tests for StorageController.
// */
//class StorageControllerTest: CoroutineTest(), KoinTest {
//    private val dummyAsset = Asset(AssetId("dummy_id"), "dummy.png", byteArrayOf(1, 2, 3))
//
//    @BeforeTest
//    fun setupTest() {
//        startTestKoin()
//    }
//
//    @AfterTest
//    fun cleanUp() {
//        stopKoin()
//    }
//
//    @Test
//    fun `createAsset should return HttpResponse with OK and asset`() = testEdifikanaApplication {
//        // Arrange
//        val call = mockk<ApplicationCall>(relaxed = true)
//        val request = CreateAssetNetworkRequest("dummy.png", byteArrayOf(1, 2, 3))
//        coEvery { call.receive<CreateAssetNetworkRequest>() } returns request
//        coEvery { storageService.createAsset(any(), any()) } returns dummyAsset
//
//        // Act
//        val response = controller.createAsset(call)
//
//        // Assert
//        assertIs<HttpResponse>(response)
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertEquals(dummyAsset.fileName, (response.body as Asset).fileName)
//    }
//
//    @Test
//    fun `getAsset should return HttpResponse with OK and asset`() = testEdifikanaApplication {
//        // Arrange
//        val call = mockk<ApplicationCall>(relaxed = true)
//        val assetId = AssetId("dummy_id")
//        coEvery { call.parameters["id"] } returns assetId.toString()
//        coEvery { storageService.getAsset(any()) } returns dummyAsset
//
//        // Act
//        val response = controller.getAsset(call)
//
//        // Assert
//        assertIs<HttpResponse>(response)
//        assertEquals(HttpStatusCode.OK, response.status)
//        assertEquals(dummyAsset.fileName, (response.body as Asset).fileName)
//    }
//}
