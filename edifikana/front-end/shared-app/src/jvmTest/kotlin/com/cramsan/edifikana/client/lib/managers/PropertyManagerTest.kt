package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.FileService
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.CoreUri
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
 * Unit tests for the PropertyManager class.
 */
class PropertyManagerTest : CoroutineTest() {
    private lateinit var propertyService: PropertyService
    private lateinit var storageManager: StorageManager
    private lateinit var fileService: FileService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: PropertyManager
    private lateinit var organizationService: OrganizationService

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyService = mockk()
        storageManager = mockk(relaxed = true)
        fileService = mockk(relaxed = true)
        organizationService = mockk()

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = PropertyManager(propertyService, storageManager, fileService, dependencies)
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
        val imageUrl = "drawable:CASA"
        coEvery { propertyService.addProperty(propertyName, address, organizationId, imageUrl) } returns Result.success(mockk())
        // Act
        val result = manager.addProperty(propertyName, address, organizationId, imageUrl)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.addProperty(propertyName, address, organizationId, imageUrl) }
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
        val imageUrl = "drawable:QUINTA"
        coEvery { propertyService.updateProperty(propertyId, name, address, imageUrl) } returns Result.success(mockk())
        // Act
        val result = manager.updateProperty(propertyId, name, address, imageUrl)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.updateProperty(propertyId, name, address, imageUrl) }
    }

    /**
     * Tests that addProperty with a custom image upload builds the correct storage path.
     */
    @Test
    fun `addProperty with custom image uses sanitized filename in storage path`() = runCoroutineTest {
        // Arrange
        val propertyName = "Cenit"
        val address = "Jiron Juan de Arona 123"
        val organizationId = OrganizationId("org-1")
        val propertyId = PropertyId("property-1")
        val imageUri = mockk<CoreUri>()
        val createdProperty = PropertyModel(propertyId, propertyName, address, organizationId)
        every { fileService.getFilename(imageUri) } returns "photo.jpg"
        coEvery { propertyService.addProperty(propertyName, address, organizationId, null) } returns Result.success(createdProperty)
        coEvery { storageManager.uploadImage(imageUri, any()) } returns Result.success("storage-ref-123")
        coEvery { propertyService.updateProperty(propertyId, propertyName, address, "storage:storage-ref-123") } returns Result.success(mockk())
        // Act
        val result = manager.addProperty(propertyName, address, organizationId, imageUri = imageUri)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { storageManager.uploadImage(imageUri, "private/properties/property-1_photo.jpg") }
    }

    /**
     * Tests that addProperty strips path traversal sequences from the filename.
     */
    @Test
    fun `addProperty with custom image strips path traversal from filename`() = runCoroutineTest {
        // Arrange
        val propertyName = "Cenit"
        val address = "Jiron Juan de Arona 123"
        val organizationId = OrganizationId("org-1")
        val propertyId = PropertyId("property-1")
        val imageUri = mockk<CoreUri>()
        val createdProperty = PropertyModel(propertyId, propertyName, address, organizationId)
        every { fileService.getFilename(imageUri) } returns "../../evil.jpg"
        coEvery { propertyService.addProperty(propertyName, address, organizationId, null) } returns Result.success(createdProperty)
        coEvery { storageManager.uploadImage(imageUri, any()) } returns Result.success("storage-ref-123")
        coEvery { propertyService.updateProperty(propertyId, propertyName, address, "storage:storage-ref-123") } returns Result.success(mockk())
        // Act
        val result = manager.addProperty(propertyName, address, organizationId, imageUri = imageUri)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { storageManager.uploadImage(imageUri, "private/properties/property-1_evil.jpg") }
    }

    /**
     * Tests that updateProperty with a custom image upload builds the correct storage path.
     */
    @Test
    fun `updateProperty with custom image uses sanitized filename in storage path`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        val name = "Cenito"
        val address = "Jiron Juan de Arona 456"
        val imageUri = mockk<CoreUri>()
        every { fileService.getFilename(imageUri) } returns "photo.jpg"
        coEvery { storageManager.uploadImage(imageUri, any()) } returns Result.success("storage-ref-123")
        coEvery { propertyService.updateProperty(propertyId, name, address, "storage:storage-ref-123") } returns Result.success(mockk())
        // Act
        val result = manager.updateProperty(propertyId, name, address, imageUri = imageUri)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { storageManager.uploadImage(imageUri, "private/properties/property-1_photo.jpg") }
    }

    /**
     * Tests that updateProperty strips path traversal sequences from the filename.
     */
    @Test
    fun `updateProperty with custom image strips path traversal from filename`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        val name = "Cenito"
        val address = "Jiron Juan de Arona 456"
        val imageUri = mockk<CoreUri>()
        every { fileService.getFilename(imageUri) } returns "../../evil.jpg"
        coEvery { storageManager.uploadImage(imageUri, any()) } returns Result.success("storage-ref-123")
        coEvery { propertyService.updateProperty(propertyId, name, address, "storage:storage-ref-123") } returns Result.success(mockk())
        // Act
        val result = manager.updateProperty(propertyId, name, address, imageUri = imageUri)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { storageManager.uploadImage(imageUri, "private/properties/property-1_evil.jpg") }
    }

    /**
     * Tests that removeProperty calls the service and returns success.
     */
    @Test
    fun `removeProperty calls service with correct arguments`() = runCoroutineTest {
        // Arrange
        val propertyId = PropertyId("property-1")
        coEvery { propertyService.removeProperty(propertyId) } returns Result.success(Unit)
        coEvery { propertyService.getPropertyList() } returns Result.success(emptyList())

        // Act
        val result = manager.removeProperty(propertyId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { propertyService.removeProperty(propertyId) }
    }
}
