package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.OrganizationService
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
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

class OrganizationControllerTest : CoroutineTest(), KoinTest {
    @BeforeTest
    fun setupTest() {
        startTestKoin()
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `test getOrganization`() = testEdifikanaApplication {
        // Setup
        val expectedResponse = readFileContent("requests/get_organization_response.json")
        val organizationService = get<OrganizationService>()
        coEvery {
            organizationService.getOrganization(OrganizationId("org123"))
        }.answers {
            Organization(
                id = OrganizationId("org123"),
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        } answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        }

        // Execute
        val response = client.get("organization/org123")

        // Verify
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getOrganizationList`() = testEdifikanaApplication {
        // Setup
        val expectedResponse = readFileContent("requests/get_organization_list_response.json")
        val organizationService = get<OrganizationService>()
        coEvery {
            organizationService.getOrganizations(UserId("user456"))
        }.answers {
            listOf(
                Organization(id = OrganizationId("org123")),
                Organization(id = OrganizationId("org456"))
            )
        }
        val contextRetriever = get<ContextRetriever>()
        coEvery {
            contextRetriever.getContext(any())
        }.answers {
            ClientContext.AuthenticatedClientContext(
                userInfo = mockk(),
                userId = UserId("user456"),
            )
        }

        // Execute
        val response = client.get("organization")

        // Verify
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }
}
