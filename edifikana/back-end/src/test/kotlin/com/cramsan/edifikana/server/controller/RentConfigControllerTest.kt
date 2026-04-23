@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.RentConfigService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class RentConfigControllerTest : CoroutineTest(), KoinTest {

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
    // getRentConfig
    // -------------------------------------------------------------------------

    @Test
    fun `test getRentConfig returns 200 when found and user has EMPLOYEE role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_rent_config_response.json")
        val rentConfigService = get<RentConfigService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE) } returns true
        coEvery { rentConfigService.getRentConfig(unitId) }.answers {
            rentConfig(RentConfigId("rc123"), unitId)
        }

        // Act
        val response = client.get("rent-config/unit123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getRentConfig returns 404 when config not found`() = testBackEndApplication {
        // Arrange
        val rentConfigService = get<RentConfigService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE) } returns true
        coEvery { rentConfigService.getRentConfig(unitId) } returns null

        // Act
        val response = client.get("rent-config/unit123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getRentConfig returns 404 when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val rentConfigService = get<RentConfigService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE) } returns false

        // Act
        val response = client.get("rent-config/unit123")

        // Assert
        coVerify { rentConfigService wasNot Called }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // -------------------------------------------------------------------------
    // setRentConfig
    // -------------------------------------------------------------------------

    @Test
    fun `test setRentConfig succeeds when user has ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/set_rent_config_request.json")
        val expectedResponse = readFileContent("requests/set_rent_config_response.json")
        val rentConfigService = get<RentConfigService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN) } returns true
        coEvery {
            rentConfigService.setRentConfig(
                unitId = unitId,
                monthlyAmount = 120000.0,
                dueDay = 1,
                currency = "USD",
                updatedBy = UserId("user123"),
            )
        }.answers { rentConfig(RentConfigId("rc123"), unitId) }

        // Act
        val response = client.put("rent-config/unit123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test setRentConfig fails when user lacks ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/set_rent_config_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val rentConfigService = get<RentConfigService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN) } returns false

        // Act
        val response = client.put("rent-config/unit123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { rentConfigService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun rentConfig(
        id: RentConfigId,
        unitId: UnitId,
    ) = RentConfig(
        id = id,
        unitId = unitId,
        monthlyAmount = 120000.0,
        dueDay = 1,
        currency = "USD",
        updatedAt = Instant.fromEpochMilliseconds(0),
        updatedBy = UserId("user123"),
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}
