package com.cramsan.edifikana.client.lib.features.home.propertydetail

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StorageManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PropertyDetailViewModelTest : CoroutineTest() {

    private lateinit var viewModel: PropertyDetailViewModel
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
        viewModel = PropertyDetailViewModel(
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

    @Test
    fun `test initial state`() = runCoroutineTest {
        assertEquals(PropertyDetailUIState.Initial, viewModel.uiState.value)
        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.propertyId)
        assertEquals("", viewModel.uiState.value.name)
        assertEquals("", viewModel.uiState.value.address)
        assertFalse(viewModel.uiState.value.isEditMode)
    }

    @Test
    fun `test initialize with success loads property data`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Test Property",
            address = "123 Test Street",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:CASA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)

        viewModel.initialize(propertyId)

        assertEquals(propertyId, viewModel.uiState.value.propertyId)
        assertEquals("Test Property", viewModel.uiState.value.name)
        assertEquals("123 Test Street", viewModel.uiState.value.address)
        assertEquals("drawable:CASA", viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify { propertyManager.getProperty(propertyId) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test initialize with failure shows error snackbar`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        coEvery { propertyManager.getProperty(propertyId) } returns Result.failure(
            Exception("Network error")
        )

        val verificationJob = launch {
            windowEventBus.events.test {
                val event = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(event.message.contains("Failed to load property"))
                assertTrue(event.message.contains("Network error"))
            }
        }

        viewModel.initialize(propertyId)
        verificationJob.join()

        assertFalse(viewModel.uiState.value.isLoading)
        coVerify { propertyManager.getProperty(propertyId) }
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test toggleEditMode toggles edit mode state`() = runCoroutineTest {
        assertFalse(viewModel.uiState.value.isEditMode)

        viewModel.toggleEditMode()
        assertTrue(viewModel.uiState.value.isEditMode)

        viewModel.toggleEditMode()
        assertFalse(viewModel.uiState.value.isEditMode)
    }

    @Test
    fun `test cancelEdit reverts changes and exits edit mode`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Original Name",
            address = "Original Address",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:QUINTA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)

        viewModel.initialize(propertyId)
        viewModel.toggleEditMode()
        viewModel.onNameChanged("Modified Name")
        viewModel.onAddressChanged("Modified Address")
        viewModel.onImageUrlChanged("drawable:CASA")

        assertEquals("Modified Name", viewModel.uiState.value.name)
        assertEquals("Modified Address", viewModel.uiState.value.address)
        assertEquals("drawable:CASA", viewModel.uiState.value.imageUrl)
        assertTrue(viewModel.uiState.value.isEditMode)

        viewModel.cancelEdit()

        assertEquals("Original Name", viewModel.uiState.value.name)
        assertEquals("Original Address", viewModel.uiState.value.address)
        assertEquals("drawable:QUINTA", viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isEditMode)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test cancelEdit without propertyId does nothing`() = runCoroutineTest {
        viewModel.toggleEditMode()
        assertTrue(viewModel.uiState.value.isEditMode)

        viewModel.cancelEdit()

        assertTrue(viewModel.uiState.value.isEditMode)
    }

    @Test
    fun `test onNameChanged updates name in UI state`() = runCoroutineTest {
        viewModel.onNameChanged("New Name")
        assertEquals("New Name", viewModel.uiState.value.name)
    }

    @Test
    fun `test onAddressChanged updates address in UI state`() = runCoroutineTest {
        viewModel.onAddressChanged("New Address")
        assertEquals("New Address", viewModel.uiState.value.address)
    }

    @Test
    fun `test onImageUrlChanged updates imageUrl in UI state`() = runCoroutineTest {
        viewModel.onImageUrlChanged("drawable:L_DEPA")
        assertEquals("drawable:L_DEPA", viewModel.uiState.value.imageUrl)
    }

    @Test
    fun `test saveProperty with success updates property and shows success message`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Test Property",
            address = "123 Test Street",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:L_DEPA",
        )
        val updatedProperty = PropertyModel(
            id = propertyId,
            name = "Updated Name",
            address = "Updated Address",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:M_DEPA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)
        coEvery {
            propertyManager.updateProperty(propertyId, "Updated Name", "Updated Address", "drawable:M_DEPA")
        } returns Result.success(updatedProperty)

        viewModel.initialize(propertyId)
        viewModel.toggleEditMode()
        viewModel.onNameChanged("Updated Name")
        viewModel.onAddressChanged("Updated Address")
        viewModel.onImageUrlChanged("drawable:M_DEPA")

        val verificationJob = launch {
            windowEventBus.events.test {
                val event = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertEquals("Property updated successfully", event.message)
            }
        }

        viewModel.saveProperty()
        verificationJob.join()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isEditMode)
        coVerify { propertyManager.updateProperty(propertyId, "Updated Name", "Updated Address", "drawable:M_DEPA") }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test saveProperty with failure shows error snackbar`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Test Property",
            address = "123 Test Street",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:S_DEPA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)
        coEvery {
            propertyManager.updateProperty(propertyId, any(), any(), any(), any())
        } returns Result.failure(Exception("Update failed"))

        viewModel.initialize(propertyId)
        viewModel.toggleEditMode()

        val verificationJob = launch {
            windowEventBus.events.test {
                val event = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(event.message.contains("Failed to update property"))
            }
        }

        viewModel.saveProperty()
        verificationJob.join()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test saveProperty without propertyId does nothing`() = runCoroutineTest {
        viewModel.saveProperty()

        coVerify(exactly = 0) { propertyManager.updateProperty(any(), any(), any(), any()) }
    }

    @Test
    fun `test deleteProperty with success shows success message and navigates back`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Test Property",
            address = "123 Test Street",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:CASA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)
        coEvery { propertyManager.removeProperty(propertyId) } returns Result.success(Unit)

        viewModel.initialize(propertyId)

        val verificationJob = launch {
            windowEventBus.events.test {
                val snackbarEvent = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertEquals("Property deleted successfully", snackbarEvent.message)
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.deleteProperty()
        verificationJob.join()

        coVerify { propertyManager.removeProperty(propertyId) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test deleteProperty with failure shows error snackbar`() = runCoroutineTest {
        val propertyId = PropertyId("test-property-id")
        val property = PropertyModel(
            id = propertyId,
            name = "Test Property",
            address = "123 Test Street",
            organizationId = OrganizationId("org-1"),
            imageUrl = "drawable:QUINTA",
        )
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(property)
        coEvery { propertyManager.removeProperty(propertyId) } returns Result.failure(
            Exception("Delete failed")
        )

        viewModel.initialize(propertyId)

        val verificationJob = launch {
            windowEventBus.events.test {
                val event = awaitItem() as EdifikanaWindowsEvent.ShowSnackbar
                assertTrue(event.message.contains("Failed to delete property"))
                assertTrue(event.message.contains("Delete failed"))
            }
        }

        viewModel.deleteProperty()
        verificationJob.join()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test deleteProperty without propertyId does nothing`() = runCoroutineTest {
        viewModel.deleteProperty()

        coVerify(exactly = 0) { propertyManager.removeProperty(any()) }
    }
}
