package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.OrganizationService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
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

class OrganizationControllerTest : CoroutineTest(), KoinTest {
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
    fun `test getOrganization succeeds when user has required role`() = testBackEndApplication {
        // Setup
        val expectedResponse = readFileContent("requests/get_organization_response.json")
        val organizationService = get<OrganizationService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        coEvery {
            organizationService.getOrganization(orgId)
        }.answers {
            Organization(
                id = OrganizationId("org123"),
                name = "Test Organization",
                description = "Test Description",
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        } answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN)
        }.answers {
            true
        }

        // Execute
        val response = client.get("organization/org123")

        // Verify
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getOrganization fails when the user doesn't have the required perms`() = testBackEndApplication {
        // Setup
        val expectedResponse = "You are not authorized to perform this action."
        val organizationService = get<OrganizationService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        } answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN)
        }.answers {
            false
        }

        // Execute
        val response = client.get("organization/org123")

        // Verify
        coVerify { organizationService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getOrganizationList`() = testBackEndApplication {
        // Setup
        val expectedResponse = readFileContent("requests/get_organization_list_response.json")
        val organizationService = get<OrganizationService>()
        coEvery {
            organizationService.getOrganizations(UserId("user456"))
        }.answers {
            listOf(
                Organization(id = OrganizationId("org123"), name = "Org 1", description = "Description 1"),
                Organization(id = OrganizationId("org456"), name = "Org 2", description = "Description 2")
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(
                    userInfo = mockk(),
                    userId = UserId("user456"),
                )
            )
        }

        // Execute
        val response = client.get("organization")

        // Verify
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test createOrganization succeeds`() = testBackEndApplication {
        // Setup
        val requestBody = readFileContent("requests/create_organization_request.json")
        val expectedResponse = readFileContent("requests/create_organization_response.json")
        val organizationService = get<OrganizationService>()
        val userId = UserId("user456")
        coEvery {
            organizationService.createOrganization(
                userId = userId,
                name = "New Organization",
                description = "A new test organization",
            )
        }.answers {
            Result.success(
                Organization(
                    id = OrganizationId("org789"),
                    name = "New Organization",
                    description = "A new test organization",
                )
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                SupabaseContextPayload(
                    userInfo = mockk(),
                    userId = userId,
                )
            )
        }

        // Execute
        val response = client.post("organization") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Verify
        coVerify { organizationService.createOrganization(userId, "New Organization", "A new test organization") }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateOrganization succeeds when user has required role`() = testBackEndApplication {
        // Setup
        val requestBody = readFileContent("requests/update_organization_request.json")
        val expectedResponse = readFileContent("requests/update_organization_response.json")
        val organizationService = get<OrganizationService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        coEvery {
            organizationService.updateOrganization(
                id = orgId,
                name = "Updated Organization",
                description = "Updated description",
            )
        }.answers {
            Result.success(
                Organization(
                    id = OrganizationId("org123"),
                    name = "Updated Organization",
                    description = "Updated description",
                )
            )
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN)
        }.answers {
            true
        }

        // Execute
        val response = client.put("organization/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Verify
        coVerify { organizationService.updateOrganization(orgId, "Updated Organization", "Updated description") }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updateOrganization fails when user doesn't have required perms`() = testBackEndApplication {
        // Setup
        val requestBody = readFileContent("requests/update_organization_request.json")
        val expectedResponse = "You are not authorized to perform this action."
        val organizationService = get<OrganizationService>()
        val rbacService = get<RBACService>()
        val orgId = OrganizationId("org123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        )
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            context
        }
        coEvery {
            rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN)
        }.answers {
            false
        }

        // Execute
        val response = client.put("organization/org123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Verify
        coVerify { organizationService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
