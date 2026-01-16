package com.cramsan.edifikana.client.lib.features.home.addproperty

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
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
import kotlin.test.assertTrue

class AddPropertyViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AddPropertyViewModel
    private lateinit var propertyManager: PropertyManager
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
        viewModel = AddPropertyViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            propertyManager = propertyManager
        )
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test addProperty with valid data adds property and navigates back`() = runCoroutineTest {
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
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Property $propertyName added successfully"
                    ),
                    awaitItem()
                )
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.initialize(organizationId)
        viewModel.addProperty(propertyName, address, imageUrl)
        verificationJob.join()

        coVerify { propertyManager.addProperty(propertyName, address, organizationId, imageUrl) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test addProperty with failure updates UI state with error`() = runCoroutineTest {
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val organizationId = OrganizationId("org_id_1")
        val imageUrl = "drawable:S_DEPA"
        coEvery {
            propertyManager.addProperty(propertyName, address, organizationId, imageUrl)
        } returns Result.failure(Exception("Error"))

        viewModel.addProperty(propertyName, address, imageUrl)

        assertEquals(1, exceptionHandler.exceptions.size)
        assertEquals(false, viewModel.uiState.value.isLoading)
        // TODO: Update this test once error handling is implemented in addProperty
    }
}