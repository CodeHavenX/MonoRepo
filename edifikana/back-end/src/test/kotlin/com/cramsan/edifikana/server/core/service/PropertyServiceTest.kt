package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.PropertyDatabase
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
    private lateinit var propertyDatabase: PropertyDatabase
    private lateinit var propertyService: PropertyService

    /**
     * Sets up the test environment by initializing mocks for [PropertyDatabase] and [propertyService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyDatabase = mockk()
        propertyService = PropertyService(propertyDatabase)
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
    fun `createProperty should call propertyDatabase and return property`() = runTest {
        // Arrange
        val name = "CENIT"
        val property = mockk<Property>()
        coEvery { propertyDatabase.createProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.createProperty(name)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatabase.createProperty(CreatePropertyRequest(name)) }
    }

    /**
     * Tests that updateProperty updates a property and returns it.
     */
    @Test
    fun `getProperty should call propertyDatabase and return property`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val property = mockk<Property>()
        coEvery { propertyDatabase.getProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.getProperty(propertyId)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatabase.getProperty(GetPropertyRequest(propertyId)) }
    }

    /**
     * Tests that getProperty returns null if the property is not found.
     */
    @Test
    fun `getProperty should return null if not found`() = runTest {
        // Arrange
        val propertyId = PropertyId("Muralla")
        coEvery { propertyDatabase.getProperty(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = propertyService.getProperty(propertyId)

        // Assert
        assertNull(result)
        coVerify { propertyDatabase.getProperty(GetPropertyRequest(propertyId)) }
    }

    /**
     * Tests that getProperties retrieves all properties and returns a list.q
     */
    @Test
    fun `getProperties should call propertyDatabase and return list`() = runTest {
        // Arrange
        val propertyList = listOf(mockk<Property>(), mockk<Property>())
        coEvery { propertyDatabase.getProperties(GetPropertyListsRequest(UserId("TestId1"))) } returns Result.success(
            propertyList
        )

        // Act
        val result = propertyService.getProperties(UserId("TestId1"))

        // Assert
        assertEquals(propertyList, result)
        coVerify { propertyDatabase.getProperties(GetPropertyListsRequest(UserId("TestId1"))) }
    }

    /**
     * Tests that updateProperty updates a property and returns the updated property.
     */
    @Test
    fun `updateProperty should call propertyDatabase and return updated property`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        val name = "Updated Name"
        val property = mockk<Property>()
        coEvery { propertyDatabase.updateProperty(any()) } returns Result.success(property)

        // Act
        val result = propertyService.updateProperty(propertyId, name)

        // Assert
        assertEquals(property, result)
        coVerify { propertyDatabase.updateProperty(UpdatePropertyRequest(propertyId, name)) }
    }

    /**
     * Tests that deleteProperty deletes a property and returns true.
     */
    @Test
    fun `deleteProperty should call propertyDatabase and return true`() = runTest {
        // Arrange
        val propertyId = PropertyId("Edificio")
        coEvery { propertyDatabase.deleteProperty(any()) } returns Result.success(true)

        // Act
        val result = propertyService.deleteProperty(propertyId)

        // Assert
        assertEquals(true, result)
        coVerify { propertyDatabase.deleteProperty(DeletePropertyRequest(propertyId)) }
    }
}
