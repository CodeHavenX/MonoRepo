package com.cramsan.edifikana.client.lib.features.home.addproperty

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.FileManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AddPropertyViewModel].
 */
class AddPropertyViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AddPropertyViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var storageService: StorageService
    private lateinit var fileManager: FileManager
    private lateinit var ioDependencies: IODependencies
    private lateinit var stringProvider: StringProvider
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        propertyManager = mockk(relaxed = true)
        storageService = mockk(relaxed = true)
        fileManager = mockk(relaxed = true)
        ioDependencies = mockk(relaxed = true)
        stringProvider = mockk(relaxed = true)
        viewModel = AddPropertyViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            propertyManager = propertyManager,
            storageService = storageService,
            fileManager = fileManager,
            ioDependencies = ioDependencies,
            stringProvider = stringProvider,
        )
    }

    /**
     * Test that navigateBack emits a NavigateBack event.
     */
    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        // Arrange
        val verificationJob = launch {
            windowEventBus.events.test {
                // Assert
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }

        // Act
        viewModel.navigateBack()
        verificationJob.join()
    }

    /**
     * Test that addProperty with valid data successfully adds property and navigates back.
     */
    @Test
    fun `test addProperty with valid data adds property and navigates back`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUrl = "drawable:S_DEPA"
        val newProperty = PropertyModel(
            id = PropertyId("test-id"),
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = imageUrl,
        )
        coEvery { propertyManager.addProperty(propertyName, address, organizationId, imageUrl) } returns Result.success(
            newProperty
        )

        val verificationJob = launch {
            windowEventBus.events.test {
                // Assert
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Property $propertyName added successfully"
                    ),
                    awaitItem()
                )
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl, selectedImageUri = null)
        verificationJob.join()

        // Assert
        coVerify { propertyManager.addProperty(propertyName, address, organizationId, imageUrl) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    /**
     * Test that addProperty with failure updates UI state with error.
     */
    @Test
    fun `test addProperty with failure updates UI state with error`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUrl = "drawable:S_DEPA"
        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, imageUrl)
        } returns Result.failure(Exception("Error"))

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl, selectedImageUri = null)

        // Assert
        assertEquals(1, exceptionHandler.exceptions.size)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    /**
     * Test that triggerPhotoPicker emits an OpenPhotoPicker event.
     */
    @Test
    fun `test triggerPhotoPicker emits OpenPhotoPicker event`() = runCoroutineTest {
        // Arrange
        val verificationJob = launch {
            windowEventBus.events.test {
                // Assert
                assertEquals(
                    EdifikanaWindowsEvent.OpenPhotoPicker,
                    awaitItem()
                )
            }
        }

        // Act
        viewModel.triggerPhotoPicker()
        verificationJob.join()
    }

    /**
     * Test that handleReceivedImages with empty list does not change UI state.
     */
    @Test
    fun `test handleReceivedImages with empty list does nothing`() = runCoroutineTest {
        // Act
        viewModel.handleReceivedImages(emptyList())

        // Assert
        assertNull(viewModel.uiState.value.selectedIcon)
        assertFalse(viewModel.uiState.value.isUploading)
    }

    /**
     * Test that addProperty with custom image follows the correct flow:
     * 1. Creates property (gets propertyID)
     * 2. Uploads image with propertyID in filename
     * 3. Updates property with storage reference
     */
    @Test
    fun `test addProperty with custom image creates property then uploads with propertyID`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUri = CoreUri("file:///test.jpg")
        val propertyId = PropertyId("test-property-id")
        val newProperty = PropertyModel(
            id = propertyId,
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = null,
        )
        val expectedStorageRef = "private/properties/${propertyId}_test.jpg"

        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        } returns Result.success(newProperty)

        every { fileManager.getFilename(imageUri) } returns "test.jpg"
        coEvery { fileManager.readFileBytes(imageUri) } returns Result.success(byteArrayOf(1, 2, 3))
        coEvery { fileManager.processImage(any()) } returns Result.success(byteArrayOf(1, 2, 3))

        coEvery {
            storageService.uploadFile(any(), expectedStorageRef)
        } returns Result.success(expectedStorageRef)

        coEvery {
            propertyManager.updateProperty(
                propertyId,
                propertyName,
                address,
                "storage:$expectedStorageRef"
            )
        } returns Result.success(newProperty.copy(imageUrl = "storage:$expectedStorageRef"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("added with custom image"))
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl = null, selectedImageUri = imageUri)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        }
        coVerify(exactly = 1) {
            storageService.uploadFile(any(), expectedStorageRef)
        }
        coVerify(exactly = 1) {
            propertyManager.updateProperty(
                propertyId,
                propertyName,
                address,
                "storage:$expectedStorageRef"
            )
        }
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isUploading)
    }

    /**
     * Test that addProperty with custom image handles upload failure gracefully.
     * Property is created but image is not uploaded, user is notified.
     */
    @Test
    fun `test addProperty with custom image handles upload failure gracefully`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUri = CoreUri("file:///test.jpg")
        val propertyId = PropertyId("test-id")
        val newProperty = PropertyModel(
            id = propertyId,
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = null,
        )

        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        } returns Result.success(newProperty)

        every { fileManager.getFilename(imageUri) } returns "test.jpg"
        coEvery { fileManager.readFileBytes(imageUri) } returns Result.success(byteArrayOf(1, 2, 3))
        coEvery { fileManager.processImage(any()) } returns Result.success(byteArrayOf(1, 2, 3))

        coEvery {
            storageService.uploadFile(any(), any())
        } returns Result.failure(ClientRequestExceptions.InvalidRequestException("Upload failed"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("upload failed"))
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl = null, selectedImageUri = imageUri)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        }
        coVerify(exactly = 0) {
            propertyManager.updateProperty(any(), any(), any(), any())
        }
    }

    /**
     * Test that addProperty with custom image handles property update failure gracefully.
     * Property is created and image is uploaded, but linking them fails.
     */
    @Test
    fun `test addProperty with custom image handles property update failure`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUri = CoreUri("file:///test.jpg")
        val propertyId = PropertyId("test-id")
        val newProperty = PropertyModel(
            id = propertyId,
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = null,
        )
        val storageRef = "private/properties/${propertyId}_test.jpg"

        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        } returns Result.success(newProperty)

        every { fileManager.getFilename(imageUri) } returns "test.jpg"
        coEvery { fileManager.readFileBytes(imageUri) } returns Result.success(byteArrayOf(1, 2, 3))
        coEvery { fileManager.processImage(any()) } returns Result.success(byteArrayOf(1, 2, 3))

        coEvery {
            storageService.uploadFile(any(), any())
        } returns Result.success(storageRef)

        coEvery {
            propertyManager.updateProperty(any(), any(), any(), any())
        } returns Result.failure(Exception("Update failed"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("failed to attach image"))
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl = null, selectedImageUri = imageUri)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null)
        }
        coVerify(exactly = 1) {
            storageService.uploadFile(any(), any())
        }
        coVerify(exactly = 1) {
            propertyManager.updateProperty(any(), any(), any(), any())
        }
    }
}
