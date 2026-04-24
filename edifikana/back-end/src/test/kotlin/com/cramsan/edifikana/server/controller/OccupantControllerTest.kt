@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.OccupantService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Occupant
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.ConflictException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.datetime.LocalDate
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class OccupantControllerTest : CoroutineTest(), KoinTest {

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
    // Resident RBAC — getOccupant
    // -------------------------------------------------------------------------

    @Test
    fun `getOccupant returns occupant when Resident is reading their own record`() = testBackEndApplication {
        val occupantId = OccupantId("occupant123")
        val callerId = UserId("user123")
        val occupantService = get<OccupantService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )
        val expectedResponse = readFileContent("requests/get_occupant_response.json")

        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, occupantId, UserRole.EMPLOYEE) }.answers { false }
        coEvery { occupantService.getOccupant(occupantId) }.answers {
            Occupant(
                id = occupantId,
                unitId = UnitId("unit123"),
                userId = callerId,
                addedBy = callerId,
                occupantType = OccupantType.TENANT,
                isPrimary = true,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
                status = OccupancyStatus.ACTIVE,
                addedAt = Instant.fromEpochSeconds(0),
            )
        }

        val response = client.get("occupants/occupant123")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `getOccupant returns 404 when Resident tries to read another user's record`() = testBackEndApplication {
        val occupantId = OccupantId("occupant123")
        val callerId = UserId("user123")
        val occupantService = get<OccupantService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )

        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, occupantId, UserRole.EMPLOYEE) }.answers { false }
        coEvery { occupantService.getOccupant(occupantId) }.answers {
            Occupant(
                id = occupantId,
                unitId = UnitId("unit123"),
                userId = UserId("different-user"),
                addedBy = callerId,
                occupantType = OccupantType.TENANT,
                isPrimary = true,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
                status = OccupancyStatus.ACTIVE,
                addedAt = Instant.fromEpochSeconds(0),
            )
        }

        val response = client.get("occupants/occupant123")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `getOccupant returns 404 when Resident tries to read an occupant with no linked user`() = testBackEndApplication {
        val occupantId = OccupantId("occupant123")
        val callerId = UserId("user123")
        val occupantService = get<OccupantService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )

        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, occupantId, UserRole.EMPLOYEE) }.answers { false }
        coEvery { occupantService.getOccupant(occupantId) }.answers {
            Occupant(
                id = occupantId,
                unitId = UnitId("unit123"),
                userId = null,
                addedBy = callerId,
                occupantType = OccupantType.TENANT,
                isPrimary = true,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
                status = OccupancyStatus.ACTIVE,
                addedAt = Instant.fromEpochSeconds(0),
            )
        }

        val response = client.get("occupants/occupant123")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // -------------------------------------------------------------------------
    // removeOccupant — 409 soft-remove guard
    // -------------------------------------------------------------------------

    @Test
    fun `removeOccupant returns 409 when primary occupant has other active occupants`() = testBackEndApplication {
        val occupantId = OccupantId("occupant123")
        val callerId = UserId("user123")
        val occupantService = get<OccupantService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerId)
        )

        coEvery { contextRetriever.getContext(any()) }.answers { context }
        coEvery { rbacService.hasRoleOrHigher(context, occupantId, UserRole.ADMIN) }.answers { true }
        coEvery { occupantService.removeOccupant(occupantId) }.throws(
            ConflictException("Cannot remove the primary occupant while other active occupants exist.")
        )

        val response = client.delete("occupants/occupant123")

        assertEquals(HttpStatusCode.Conflict, response.status)
    }
}
