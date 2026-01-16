package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.datastore.PropertyDatastore
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.framework.core.ktor.auth.ClientContext
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
        val clientContext = mockk<ClientContext.AuthenticatedClientContext<SupabaseContextPayload>>()
        val userId = UserId("TestUser")
        val organizationId = OrganizationId("TestOrg")
        coEvery { clientContext.payload.userId } returns userId
        val imageUrl = "drawable:CASA"
        coEvery {
            propertyDatastore.createProperty(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(property)

        // Act
        val result = propertyService.createProperty(name, address, organizationId, imageUrl, clientContext)

        // Assert
        assertEquals(property, result)
        coVerify {
            propertyDatastore.createProperty(
                name,
                address,
                userId,
                organizationId,
                imageUrl,
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
        coVerify { propertyDatastore.getProperty(propertyId) }
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
        coVerify { propertyDatastore.getProperty(propertyId) }
    }

    /**
     * Tests that getProperties retrieves all properties and returns a list.q
     */
    @Test
    fun `getProperties should call propertyDatastore and return list`() = runTest {
        // Arrange
        val propertyList = listOf(mockk<Property>(), mockk<Property>())
        coEvery { propertyDatastore.getProperties(UserId("TestId1")) } returns Result.success(
            propertyList
        )

        // Act
        val result = propertyService.getProperties(UserId("TestId1"))

        // Assert
        assertEquals(propertyList, result)
        coVerify { propertyDatastore.getProperties(UserId("TestId1")) }
    }

    /**
     * Tests that updateProperty updates a property with both name and address.
     */
    @Test
    fun `updateProperty should call propertyDatastore and return updated property`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val name = "Updated Name"
        val address = "123 Main St"
        val imageUrl = "drawable:QUINTA"
        val property = mockk<Property>()
        coEvery { propertyDatastore.updateProperty(any(), any(), any(), any()) } returns Result.success(property)

        // Act
        val result = propertyService.updateProperty(propertyId, name, address, imageUrl)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatastore.updateProperty(propertyId, name, address, imageUrl) }
    }

    /**
     * Tests that updateProperty updates only the address when name is null.
     */
    @Test
    fun `updateProperty should update only address when name is null`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val address = "456 New Address"
        val imageUrl = "drawable:L_DEPA"
        val property = mockk<Property>()
        coEvery { propertyDatastore.updateProperty(any(), any(), any(), any()) } returns Result.success(property)

        // Act
        val result = propertyService.updateProperty(propertyId, null, address, imageUrl)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatastore.updateProperty(propertyId, null, address, imageUrl) }
    }

    /**
     * Tests that updateProperty updates only the name when address is null.
     */
    @Test
    fun `updateProperty should update only name when address is null`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val name = "Updated Name Only"
        val imageUrl = "drawable:M_DEPA"
        val property = mockk<Property>()
        coEvery { propertyDatastore.updateProperty(any(), any(), any(), any()) } returns Result.success(property)

        // Act
        val result = propertyService.updateProperty(propertyId, name, null, imageUrl)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatastore.updateProperty(propertyId, name, null, imageUrl) }
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
        coVerify { propertyDatastore.deleteProperty(propertyId) }
    }
}
