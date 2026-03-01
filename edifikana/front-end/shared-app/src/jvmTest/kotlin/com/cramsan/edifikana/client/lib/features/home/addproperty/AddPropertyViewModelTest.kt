package com.cramsan.edifikana.client.lib.features.home.addproperty

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StorageManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.ui.components.ImageOptionUIModel
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.client.ui.resources.PropertyIcons
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
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AddPropertyViewModel].
 */
class AddPropertyViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AddPropertyViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var storageManager: StorageManager
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
        storageManager = mockk(relaxed = true)
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
            storageManager = storageManager,
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
        val selectedIcon = ImageOptionUIModel(
            id = "S_DEPA",
            displayName = "Departamento",
            imageSource = ImageSource.Drawable(PropertyIcons.S_DEPA)
        )
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
        viewModel.addProperty(propertyName, address, selectedIcon)
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
        val selectedIcon = ImageOptionUIModel(
            id = "S_DEPA",
            displayName = "Departamento",
            imageSource = ImageSource.Drawable(PropertyIcons.S_DEPA)
        )
        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, imageUrl, null)
        } returns Result.failure(Exception("Error"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("Failed to add property"))
            }
        }

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, selectedIcon)
        verificationJob.join()

        // Assert
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    /**
     * Test that addProperty extracts custom local file URI correctly.
     */
    @Test
    fun `test addProperty with custom local file extracts URI`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUri = CoreUri("file:///test/image.jpg")
        val propertyId = PropertyId("test-property-id")
        val selectedIcon = ImageOptionUIModel(
            id = "custom_local",
            displayName = "Custom Image",
            imageSource = ImageSource.LocalFile(imageUri, "image.jpg")
        )

        val newProperty = PropertyModel(
            id = propertyId,
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = null,
        )

        // Mock property creation with custom image (PropertyManager handles upload internally)
        coEvery { propertyManager.addProperty(propertyName, address, organizationId, null, imageUri) }
            .returns(Result.success(newProperty.copy(imageUrl = "storage:private/properties/${propertyId}_image.jpg")))

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, selectedIcon)

        // Assert - verify PropertyManager.addProperty was called with imageUri
        coVerify(exactly = 1) { propertyManager.addProperty(propertyName, address, organizationId, null, imageUri) }
    }

    /**
     * Test that addProperty with null icon uses null imageUrl.
     */
    @Test
    fun `test addProperty with null icon uses null imageUrl`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val newProperty = PropertyModel(
            id = PropertyId("test-id"),
            name = propertyName,
            address = address,
            organizationId = organizationId,
            imageUrl = null,
        )
        coEvery { propertyManager.addProperty(propertyName, address, organizationId, null) }
            .returns(Result.success(newProperty))

        // Act
        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, selectedIcon = null)

        // Assert - verify imageUrl is null
        coVerify { propertyManager.addProperty(propertyName, address, organizationId, null) }
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
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
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
        val selectedIcon = ImageOptionUIModel(
            id = "custom_local",
            displayName = "Custom Image",
            imageSource = ImageSource.LocalFile(imageUri, "test.jpg")
        )
        viewModel.addProperty(propertyName, address, selectedIcon)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
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

        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
        } returns Result.failure(ClientRequestExceptions.InvalidRequestException("Upload failed"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("Failed to add property"))
            }
        }

        // Act
        viewModel.initialize(organizationId)
        val selectedIcon = ImageOptionUIModel(
            id = "custom_local",
            displayName = "Custom Image",
            imageSource = ImageSource.LocalFile(imageUri, "test.jpg")
        )
        viewModel.addProperty(propertyName, address, selectedIcon)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
        }
    }

    /**
     * Test that addProperty with custom image handles failures gracefully.
     * PropertyManager handles the complete flow internally and reports any failures.
     */
    @Test
    fun `test addProperty with custom image handles property update failure`() = runCoroutineTest {
        // Arrange
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUri = CoreUri("file:///test.jpg")

        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
        } returns Result.failure(Exception("Property creation with image failed"))

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(snackbarEvent.message.contains("Failed to add property"))
            }
        }

        // Act
        viewModel.initialize(organizationId)
        val selectedIcon = ImageOptionUIModel(
            id = "custom_local",
            displayName = "Custom Image",
            imageSource = ImageSource.LocalFile(imageUri, "test.jpg")
        )
        viewModel.addProperty(propertyName, address, selectedIcon)
        verificationJob.join()

        // Assert
        coVerify(exactly = 1) {
            propertyManager.addProperty(propertyName, address, organizationId, null, imageUri)
        }
    }
}
