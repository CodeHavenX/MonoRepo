package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest

/**
 * Test class for [PropertyService].
 */
class PropertyServiceTest {
    private lateinit var propertyDatastore: PropertyDatastore
    private lateinit var propertyService: PropertyService

    /**
     * Sets up the test environment by initializing mocks for [PropertyDatastore] and [propertyService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyDatastore = mockk()
        propertyService = PropertyService(propertyDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createProperty creates a property and returns it.
     */
    @Test
    fun `createProperty should call propertyDatastore and return property`() = runTest {
        // Arrange
        val name = "CENIT"
        val address = "123 Main St"
        val property = mockk<Property>()
        val clientContext = mockk<ClientContext.AuthenticatedClientContext>()
        val userId = UserId("TestUser")
        val organizationId = OrganizationId("TestOrg")
        coEvery { clientContext.userId } returns userId
        coEvery { propertyDatastore.createProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.createProperty(name, address, organizationId, clientContext)

        // Assert
        assertEquals(property, result)
        coVerify {
            propertyDatastore.createProperty(
                CreatePropertyRequest(
                    name,
                    address,
                    userId,
                    organizationId,
                )
            )
        }
    }

    /**
     * Tests that updateProperty updates a property and returns it.
     */
    @Test
    fun `getProperty should call propertyDatastore and return property`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val property = mockk<Property>()
        coEvery { propertyDatastore.getProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.getProperty(propertyId)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatastore.getProperty(GetPropertyRequest(propertyId)) }
    }

    /**
     * Tests that getProperty returns null if the property is not found.
     */
    @Test
    fun `getProperty should return null if not found`() = runTest {
        // Arrange
        val propertyId = PropertyId("Muralla")
        coEvery { propertyDatastore.getProperty(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = propertyService.getProperty(propertyId)

        // Assert
        assertNull(result)
        coVerify { propertyDatastore.getProperty(GetPropertyRequest(propertyId)) }
    }

    /**
     * Tests that getProperties retrieves all properties and returns a list.q
     */
    @Test
    fun `getProperties should call propertyDatastore and return list`() = runTest {
        // Arrange
        val propertyList = listOf(mockk<Property>(), mockk<Property>())
        coEvery { propertyDatastore.getProperties(GetPropertyListsRequest(UserId("TestId1"))) } returns Result.success(
            propertyList
        )

        // Act
        val result = propertyService.getProperties(UserId("TestId1"))

        // Assert
        assertEquals(propertyList, result)
        coVerify { propertyDatastore.getProperties(GetPropertyListsRequest(UserId("TestId1"))) }
    }

    /**
     * Tests that updateProperty updates a property and returns the updated property.
     */
    @Test
    fun `updateProperty should call propertyDatastore and return updated property`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val name = "Updated Name"
        val property = mockk<Property>()
        coEvery { propertyDatastore.updateProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.updateProperty(propertyId, name)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatastore.updateProperty(UpdatePropertyRequest(propertyId, name)) }
    }

    /**
     * Tests that deleteProperty deletes a property and returns true.
     */
    @Test
    fun `deleteProperty should call propertyDatastore and return true`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        coEvery { propertyDatastore.deleteProperty(any()) } returns Result.success(true)

        // Act
        val result = propertyService.deleteProperty(propertyId)

        // Assert
        assertEquals(true, result)
        coVerify { propertyDatastore.deleteProperty(DeletePropertyRequest(propertyId)) }
    }
}
