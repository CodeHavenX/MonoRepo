package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.CommonAreaService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTime::class)
class CommonAreaControllerTest : CoroutineTest(), KoinTest {

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

    // -------------------------------------------------------------------------
    // createCommonArea
    // -------------------------------------------------------------------------

    @Test
    fun `test createCommonArea succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_common_area_request.json")
        val expectedResponse = readFileContent("requests/create_common_area_response.json")
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        coEvery {
            commonAreaService.createCommonArea(
                propertyId = PropertyId("property123"),
                name = "Main Lobby",
                type = CommonAreaType.LOBBY,
                description = "Main entrance lobby",
            )
        }.answers {
            commonArea(CommonAreaId("area123"), PropertyId("property123"))
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER) } returns true

        // Act
        val response = client.post("common-area") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test createCommonArea fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_common_area_request.json")
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER) } returns false

        // Act
        val response = client.post("common-area") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { commonAreaService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // getCommonArea
    // -------------------------------------------------------------------------

    @Test
    fun `test getCommonArea returns 200 when found and user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_common_area_response.json")
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        coEvery { commonAreaService.getCommonArea(areaId) }.answers {
            commonArea(areaId, PropertyId("property123"))
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns true

        // Act
        val response = client.get("common-area/area123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getCommonArea returns 404 when area is not found`() = testBackEndApplication {
        // Arrange
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        coEvery { commonAreaService.getCommonArea(areaId) } returns null
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns true

        // Act
        val response = client.get("common-area/area123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getCommonArea returns 404 when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns false

        // Act
        val response = client.get("common-area/area123")

        // Assert
        coVerify { commonAreaService wasNot Called }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // -------------------------------------------------------------------------
    // getCommonAreasForProperty
    // -------------------------------------------------------------------------

    @Test
    fun `test getCommonAreasForProperty succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_common_areas_for_property_response.json")
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val propertyId = PropertyId("property123")
        coEvery { commonAreaService.getCommonAreasForProperty(propertyId) }.answers {
            listOf(commonArea(CommonAreaId("area123"), propertyId))
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, propertyId, UserRole.MANAGER) } returns true

        // Act
        val response = client.get("common-area/by-property/property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getCommonAreasForProperty fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val propertyId = PropertyId("property123")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, propertyId, UserRole.MANAGER) } returns false

        // Act
        val response = client.get("common-area/by-property/property123")

        // Assert
        coVerify { commonAreaService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // updateCommonArea
    // -------------------------------------------------------------------------

    @Test
    fun `test updateCommonArea succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_common_area_request.json")
        val expectedResponse = readFileContent("requests/update_common_area_response.json")
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        coEvery {
            commonAreaService.updateCommonArea(
                commonAreaId = areaId,
                name = "Updated Lobby",
                type = CommonAreaType.LOBBY,
                description = null,
            )
        }.answers {
            commonArea(areaId, PropertyId("property123"), name = "Updated Lobby", description = null)
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns true

        // Act
        val response = client.put("common-area/area123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateCommonArea fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_common_area_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns false

        // Act
        val response = client.put("common-area/area123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { commonAreaService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // deleteCommonArea
    // -------------------------------------------------------------------------

    @Test
    fun `test deleteCommonArea succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        coEvery { commonAreaService.deleteCommonArea(areaId) } returns true
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns true

        // Act
        val response = client.delete("common-area/area123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteCommonArea fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val commonAreaService = get<CommonAreaService>()
        val rbacService = get<RBACService>()
        val areaId = CommonAreaId("area123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user123"),
            )
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, areaId, UserRole.MANAGER) } returns false

        // Act
        val response = client.delete("common-area/area123")

        // Assert
        coVerify { commonAreaService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun commonArea(
        id: CommonAreaId,
        propertyId: PropertyId,
        name: String = "Main Lobby",
        type: CommonAreaType = CommonAreaType.LOBBY,
        description: String? = "Main entrance lobby",
    ) = CommonArea(
        id = id,
        propertyId = propertyId,
        name = name,
        type = type,
        description = description,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}
