package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrganizationServiceTest {
    private lateinit var organizationDatastore: OrganizationDatastore
    private lateinit var organizationService: OrganizationService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        organizationDatastore = mockk()
        organizationService = OrganizationService(organizationDatastore)
    }

    @Test
    fun `getOrganization returns organization when found`() = runTest {
        val orgId = OrganizationId("org-1")
        val organization = mockk<Organization>()
        coEvery {
            organizationDatastore.getOrganization(orgId)
        } returns Result.success(organization)

        val result = organizationService.getOrganization(orgId)
        assertEquals(organization, result)
    }

    @Test
    fun `getOrganization returns null when not found`() = runTest {
        val orgId = OrganizationId("org-2")
        coEvery { organizationDatastore.getOrganization(orgId) } returns Result.success(null)

        val result = organizationService.getOrganization(orgId)
        assertNull(result)
    }

    @Test
    fun `getOrganizations returns list of organizations`() = runTest {
        val userId = UserId("user-1")
        val organizations = listOf(mockk<Organization>(), mockk<Organization>())
        coEvery { organizationDatastore.getOrganizationsForUser(userId) } returns Result.success(organizations)

        val result = organizationService.getOrganizations(userId)
        assertEquals(organizations, result)
    }
}
