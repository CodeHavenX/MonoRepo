@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.UnitService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Unit
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class UnitControllerTest : CoroutineTest(), KoinTest {

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
    fun `test createUnit succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_unit_request.json")
        val expectedResponse = readFileContent("requests/create_unit_response.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        coEvery {
            unitService.createUnit(
                propertyId = PropertyId("property123"),
                unitNumber = "101A",
                bedrooms = 2,
                bathrooms = 1,
                sqFt = 750,
                floor = 1,
                notes = null,
            )
        }.answers {
            Unit(
                id = UnitId("unit123"),
                propertyId = PropertyId("property123"),
                orgId = OrganizationId("org123"),
                unitNumber = "101A",
                bedrooms = 2,
                bathrooms = 1,
                sqFt = 750,
                floor = 1,
                notes = null,
                createdAt = Instant.fromEpochSeconds(0),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER)
        }.answers { true }

        // Act
        val response = client.post("unit") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test createUnit fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_unit_request.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER)
        }.answers { false }

        // Act
        val response = client.post("unit") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { unitService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUnit succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_unit_response.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        coEvery { unitService.getUnit(unitId) }.answers {
            Unit(
                id = UnitId("unit123"),
                propertyId = PropertyId("property123"),
                orgId = OrganizationId("org123"),
                unitNumber = "101A",
                bedrooms = 2,
                bathrooms = 1,
                sqFt = 750,
                floor = 1,
                notes = null,
                createdAt = Instant.fromEpochSeconds(0),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE)
        }.answers { true }

        // Act
        val response = client.get("unit/unit123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUnit returns not found when unit does not exist`() = testBackEndApplication {
        // Arrange
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        coEvery { unitService.getUnit(unitId) }.answers { null }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE)
        }.answers { true }

        // Act
        val response = client.get("unit/unit123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getUnit fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE)
        }.answers { false }

        // Act
        val response = client.get("unit/unit123")

        // Assert
        coVerify { unitService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUnits succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_units_response.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        coEvery {
            unitService.getUnits(PropertyId("property123"))
        }.answers {
            listOf(
                Unit(
                    id = UnitId("unit123"),
                    propertyId = PropertyId("property123"),
                    orgId = OrganizationId("org123"),
                    unitNumber = "101A",
                    bedrooms = 2,
                    bathrooms = 1,
                    sqFt = 750,
                    floor = 1,
                    notes = null,
                    createdAt = Instant.fromEpochSeconds(0),
                ),
                Unit(
                    id = UnitId("unit456"),
                    propertyId = PropertyId("property123"),
                    orgId = OrganizationId("org123"),
                    unitNumber = "202B",
                    bedrooms = 3,
                    bathrooms = 2,
                    sqFt = 1100,
                    floor = 2,
                    notes = "Corner unit",
                    createdAt = Instant.fromEpochSeconds(0),
                ),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.EMPLOYEE)
        }.answers { true }

        // Act
        val response = client.get("unit?property_id=property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getUnits fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, OrganizationId("org123"), UserRole.EMPLOYEE)
        }.answers { false }

        // Act
        val response = client.get("unit?org_id=org123")

        // Assert
        coVerify { unitService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateUnit succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_unit_request.json")
        val expectedResponse = readFileContent("requests/update_unit_response.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        coEvery {
            unitService.updateUnit(
                unitId = unitId,
                unitNumber = "101B",
                bedrooms = 3,
                bathrooms = 2,
                sqFt = 900,
                floor = 1,
                notes = "Renovated",
            )
        }.answers {
            Unit(
                id = UnitId("unit123"),
                propertyId = PropertyId("property123"),
                orgId = OrganizationId("org123"),
                unitNumber = "101B",
                bedrooms = 3,
                bathrooms = 2,
                sqFt = 900,
                floor = 1,
                notes = "Renovated",
                createdAt = Instant.fromEpochSeconds(0),
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.MANAGER)
        }.answers { true }

        // Act
        val response = client.put("unit/unit123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateUnit fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_unit_request.json")
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.MANAGER)
        }.answers { false }

        // Act
        val response = client.put("unit/unit123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { unitService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test deleteUnit succeeds when user has required role`() = testBackEndApplication {
        // Arrange
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        coEvery { unitService.deleteUnit(unitId) }.answers { true }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN)
        }.answers { true }

        // Act
        val response = client.delete("unit/unit123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteUnit fails when user lacks required role`() = testBackEndApplication {
        // Arrange
        val unitService = get<UnitService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery {
            rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN)
        }.answers { false }

        // Act
        val response = client.delete("unit/unit123")

        // Assert
        coVerify { unitService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
