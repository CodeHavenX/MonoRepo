package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.network.asset.StorageResourceType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.ASSET_2
import com.cramsan.edifikana.server.utils.BUCKET_ID
import com.cramsan.edifikana.server.utils.PROPERTY_1
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
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

    // region getSignedDownload

    @Test
    fun `getSignedDownload should return OK and signed URL when user is authorized`() =
        testBackEndApplication {
            // Arrange
            val expectedResponse = readFileContent("requests/get_signed_download_response.json")
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery { storageService.getSignedDownloadUrl(AssetId(ASSET_2.id.assetId)) } returns ASSET_2
            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), any()) } returns true

            // Act
            val response =
                client.get(
                "storage/signed-download" +
                    "?asset_id=${ASSET_2.id.assetId}" +
                    "&resource_type=${StorageResourceType.PROPERTY}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `getSignedDownload should return 401 when user is not authorized`() =
        testBackEndApplication {
            // Arrange
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), any()) } returns false

            // Act
            val response =
                client.get(
                "storage/signed-download" +
                    "?asset_id=${ASSET_2.id.assetId}" +
                    "&resource_type=${StorageResourceType.PROPERTY}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            coVerify { storageService wasNot Called }
        }

    // endregion

    // region createSignedUpload

    @Test
    fun `createSignedUpload should return OK and signed upload URL when user is authorized`() =
        testBackEndApplication {
            // Arrange
            val expectedResponse = readFileContent("requests/create_signed_upload_response.json")
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery { storageService.getSignedUploadUrl(ASSET_2.fileName, BUCKET_ID) } returns ASSET_2
            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), UserRole.MANAGER) } returns true

            // Act
            val response =
                client.post(
                "storage/signed-upload" +
                    "?filename=${ASSET_2.fileName}" +
                    "&bucket_id=$BUCKET_ID" +
                    "&resource_type=${StorageResourceType.PROPERTY}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(expectedResponse, response.bodyAsText())
        }

    @Test
    fun `createSignedUpload should return 401 when user lacks required role`() =
        testBackEndApplication {
            // Arrange
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), UserRole.MANAGER) } returns false

            // Act
            val response =
                client.post(
                "storage/signed-upload" +
                    "?filename=${ASSET_2.fileName}" +
                    "&bucket_id=$BUCKET_ID" +
                    "&resource_type=${StorageResourceType.PROPERTY}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            coVerify { storageService wasNot Called }
        }

    @Test
    fun `createSignedUpload should return OK for TIME_CARD when building account has EMPLOYEE role`() =
        testBackEndApplication {
            // Arrange
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery { storageService.getSignedUploadUrl(ASSET_2.fileName, BUCKET_ID) } returns ASSET_2
            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), UserRole.EMPLOYEE) } returns true

            // Act
            val response =
                client.post(
                "storage/signed-upload" +
                    "?filename=${ASSET_2.fileName}" +
                    "&bucket_id=$BUCKET_ID" +
                    "&resource_type=${StorageResourceType.TIME_CARD}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `createSignedUpload should return 401 for TIME_CARD when building account is not in org`() =
        testBackEndApplication {
            // Arrange
            val storageService = get<StorageService>()
            val rbacService = get<RBACService>()
            val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

            coEvery {
                contextRetriever.getContext(
                    any(),
                )
            }.answers { mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>() }
            coEvery { rbacService.hasRoleOrHigher(any(), any<PropertyId>(), UserRole.EMPLOYEE) } returns false

            // Act
            val response =
                client.post(
                "storage/signed-upload" +
                    "?filename=${ASSET_2.fileName}" +
                    "&bucket_id=$BUCKET_ID" +
                    "&resource_type=${StorageResourceType.TIME_CARD}" +
                    "&resource_id=${PROPERTY_1.id.propertyId}",
            )

            // Assert
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            coVerify { storageService wasNot Called }
        }

    // endregion
}
