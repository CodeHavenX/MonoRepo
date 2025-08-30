package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the PropertyManager class.
 */
class PropertyManagerTest : CoroutineTest() {
    private lateinit var propertyService: PropertyService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: PropertyManager

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyService = mockk()

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = PropertyManager(propertyService, dependencies)
    }

    /**
     * Tests that getPropertyList returns the expected list from the service.
     */
    @Test
    fun `getPropertyList returns property list`() = runCoroutineTest {
        // Arrange
        val propertyList = listOf(mockk<PropertyModel>(), mockk<PropertyModel>())
        coEvery { propertyService.getPropertyList() } returns Result.success(propertyList)
        // Act
        val result = manager.getPropertyList()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(propertyList, result.getOrNull())
        coVerify { propertyService.getPropertyList() }
    }

    /**
     * Tests that setActiveProperty calls the service and returns success.
     */
    @Test
    fun `setActiveProperty calls service`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        coEvery { propertyService.setActiveProperty(propertyId) } returns Result.success(Unit)
        // Act
        val result = manager.setActiveProperty(propertyId)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.setActiveProperty(propertyId) }
    }

    /**
     * Tests that activeProperty returns the StateFlow from the service.
     */
    @Test
    fun `activeProperty returns StateFlow`() {
        // Arrange
        val stateFlow = MutableStateFlow<PropertyId?>(PropertyId("property-1"))
        every { propertyService.activeProperty() } returns stateFlow
        // Act
        val result = manager.activeProperty()
        // Assert
        assertEquals(stateFlow, result)
        coVerify(exactly = 0) { propertyService.setActiveProperty(any()) }
    }

    /**
     * Tests that getProperty returns the expected property from the service.
     */
    @Test
    fun `getProperty returns property`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        val property = mockk<PropertyModel>()
        coEvery { propertyService.getProperty(propertyId) } returns Result.success(property)
        // Act
        val result = manager.getProperty(propertyId)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(property, result.getOrNull())
        coVerify { propertyService.getProperty(propertyId) }
    }

    /**
     * Tests that addProperty calls the service and returns success.
     */
    @Test
    fun `addProperty calls service with correct arguments`() = runCoroutineTest {
        // Arrange
        val propertyName = "Cenit"
        val address = "Jiron Juan de Arona 123"
        val organizationId = OrganizationId("org-1")
        coEvery { propertyService.addProperty(propertyName, address, organizationId) } returns Result.success(mockk())
        // Act
        val result = manager.addProperty(propertyName, address, organizationId)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.addProperty(propertyName, address, organizationId) }
    }

    /**
     * Tests that updateProperty calls the service and returns success.
     */
    @Test
    fun `updateProperty calls service with correct arguments`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        val name = "Cenito"
        val address = "Jiron Juan de Arona 456"
        coEvery { propertyService.updateProperty(propertyId, name, address) } returns Result.success(mockk())
        // Act
        val result = manager.updateProperty(propertyId, name, address)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.updateProperty(propertyId, name, address) }
    }

    /**
     * Tests that removeProperty calls the service and returns success.
     */
    @Test
    fun `removeProperty calls service with correct arguments`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        coEvery { propertyService.removeProperty(propertyId) } returns Result.success(Unit)
        // Act
        val result = manager.removeProperty(propertyId)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.removeProperty(propertyId) }
    }
}
