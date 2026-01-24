package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the OrganizationManager class.
 */
class OrganizationManagerTest : CoroutineTest() {
    private lateinit var organizationService: OrganizationService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: OrganizationManager

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        organizationService = mockk()

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = OrganizationManager(organizationService, dependencies)
    }

    /**
     * Tests that getOrganization returns the expected organization from the service.
     */
    @Test
    fun `getOrganization returns organization`() = runCoroutineTest {
        // Arrange
        val organizationId = OrganizationId("org-1")
        val organization = Organization(
            id = organizationId,
            name = "Test Organization",
            description = "Test Description",
        )
        coEvery { organizationService.getOrganization(organizationId) } returns Result.success(organization)

        // Act
        val result = manager.getOrganization(organizationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(organization, result.getOrNull())
        coVerify { organizationService.getOrganization(organizationId) }
    }

    /**
     * Tests that getOrganization returns failure when service fails.
     */
    @Test
    fun `getOrganization returns failure when service fails`() = runCoroutineTest {
        // Arrange
        val organizationId = OrganizationId("org-1")
        val exception = RuntimeException("Network error")
        coEvery { organizationService.getOrganization(organizationId) } returns Result.failure(exception)

        // Act
        val result = manager.getOrganization(organizationId)

        // Assert
        assertTrue(result.isFailure)
        coVerify { organizationService.getOrganization(organizationId) }
    }

    /**
     * Tests that getOrganizations returns the expected list from the service.
     */
    @Test
    fun `getOrganizations returns organization list`() = runCoroutineTest {
        // Arrange
        val organizationList = listOf(
            Organization(
                id = OrganizationId("org-1"),
                name = "Organization 1",
                description = "Description 1",
            ),
            Organization(
                id = OrganizationId("org-2"),
                name = "Organization 2",
                description = "Description 2",
            ),
        )
        coEvery { organizationService.getOrganizations() } returns Result.success(organizationList)

        // Act
        val result = manager.getOrganizations()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(organizationList, result.getOrNull())
        coVerify { organizationService.getOrganizations() }
    }

    /**
     * Tests that getOrganizations returns failure when service fails.
     */
    @Test
    fun `getOrganizations returns failure when service fails`() = runCoroutineTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { organizationService.getOrganizations() } returns Result.failure(exception)

        // Act
        val result = manager.getOrganizations()

        // Assert
        assertTrue(result.isFailure)
        coVerify { organizationService.getOrganizations() }
    }

    /**
     * Tests that createOrganization calls the service and returns success.
     */
    @Test
    fun `createOrganization calls service with correct arguments`() = runCoroutineTest {
        // Arrange
        val name = "New Organization"
        val description = "New Description"
        val createdOrganization = Organization(
            id = OrganizationId("org-new"),
            name = name,
            description = description,
        )
        coEvery { organizationService.createOrganization(name, description) } returns Result.success(createdOrganization)

        // Act
        val result = manager.createOrganization(name, description)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(createdOrganization, result.getOrNull())
        coVerify { organizationService.createOrganization(name, description) }
    }

    /**
     * Tests that createOrganization returns failure when service fails.
     */
    @Test
    fun `createOrganization returns failure when service fails`() = runCoroutineTest {
        // Arrange
        val name = "New Organization"
        val description = "New Description"
        val exception = RuntimeException("Creation failed")
        coEvery { organizationService.createOrganization(name, description) } returns Result.failure(exception)

        // Act
        val result = manager.createOrganization(name, description)

        // Assert
        assertTrue(result.isFailure)
        coVerify { organizationService.createOrganization(name, description) }
    }
}
