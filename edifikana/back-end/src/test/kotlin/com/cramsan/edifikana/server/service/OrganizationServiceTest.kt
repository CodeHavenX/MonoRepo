package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun `createOrganization succeeds and adds user as owner`() = runTest {
        // Arrange
        val userId = UserId("user-1")
        val orgName = "Test Organization"
        val orgDescription = "Test Description"
        val orgId = OrganizationId("org-1")
        val organization = Organization(
            id = orgId,
            name = orgName,
            description = orgDescription,
        )
        coEvery {
            organizationDatastore.createOrganization(orgName, orgDescription)
        } returns Result.success(organization)
        coEvery {
            organizationDatastore.addUserToOrganization(userId, orgId, UserRole.OWNER)
        } returns Result.success(Unit)

        // Act
        val result = organizationService.createOrganization(userId, orgName, orgDescription)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(organization, result.getOrNull())
        coVerify { organizationDatastore.createOrganization(orgName, orgDescription) }
        coVerify { organizationDatastore.addUserToOrganization(userId, orgId, UserRole.OWNER) }
    }

    @Test
    fun `createOrganization fails when organization creation fails`() = runTest {
        // Arrange
        val userId = UserId("user-1")
        val orgName = "Test Organization"
        val orgDescription = "Test Description"
        val exception = RuntimeException("Database error")
        coEvery {
            organizationDatastore.createOrganization(orgName, orgDescription)
        } returns Result.failure(exception)

        // Act
        val result = organizationService.createOrganization(userId, orgName, orgDescription)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { organizationDatastore.createOrganization(orgName, orgDescription) }
        coVerify(exactly = 0) { organizationDatastore.addUserToOrganization(any(), any(), any()) }
    }

    @Test
    fun `createOrganization cleans up organization when adding user fails`() = runTest {
        // Arrange
        val userId = UserId("user-1")
        val orgName = "Test Organization"
        val orgDescription = "Test Description"
        val orgId = OrganizationId("org-1")
        val organization = Organization(
            id = orgId,
            name = orgName,
            description = orgDescription,
        )
        val addUserException = RuntimeException("Failed to add user")
        coEvery {
            organizationDatastore.createOrganization(orgName, orgDescription)
        } returns Result.success(organization)
        coEvery {
            organizationDatastore.addUserToOrganization(userId, orgId, UserRole.OWNER)
        } returns Result.failure(addUserException)
        coEvery {
            organizationDatastore.deleteOrganization(orgId)
        } returns Result.success(true)

        // Act
        val result = organizationService.createOrganization(userId, orgName, orgDescription)

        // Assert
        assertTrue(result.isFailure)
        coVerify { organizationDatastore.createOrganization(orgName, orgDescription) }
        coVerify { organizationDatastore.addUserToOrganization(userId, orgId, UserRole.OWNER) }
        coVerify { organizationDatastore.deleteOrganization(orgId) }
    }

    @Test
    fun `updateOrganization succeeds`() = runTest {
        // Arrange
        val orgId = OrganizationId("org-1")
        val newName = "Updated Organization"
        val newDescription = "Updated Description"
        val updatedOrganization = Organization(
            id = orgId,
            name = newName,
            description = newDescription,
        )
        coEvery {
            organizationDatastore.updateOrganization(orgId, newName, newDescription)
        } returns Result.success(updatedOrganization)

        // Act
        val result = organizationService.updateOrganization(orgId, newName, newDescription)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(updatedOrganization, result.getOrNull())
        coVerify { organizationDatastore.updateOrganization(orgId, newName, newDescription) }
    }

    @Test
    fun `updateOrganization fails when datastore fails`() = runTest {
        // Arrange
        val orgId = OrganizationId("org-1")
        val newName = "Updated Organization"
        val newDescription = "Updated Description"
        val exception = RuntimeException("Database error")
        coEvery {
            organizationDatastore.updateOrganization(orgId, newName, newDescription)
        } returns Result.failure(exception)

        // Act
        val result = organizationService.updateOrganization(orgId, newName, newDescription)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { organizationDatastore.updateOrganization(orgId, newName, newDescription) }
    }
}
